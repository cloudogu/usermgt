import {useEffect, useState} from "react";
import {useSearchParams} from "react-router-dom";
import {useAPI} from "./useAPI";
import type {QueryOptions} from "./useAPI";

export const PAGE_QUERY_PARAM = "p";
export const SEARCH_QUERY_PARAM = "q";
export const LINES_PER_PAGE_QUERY_PARAM = "l";
const DEFAULT_PAGE_SIZE = 20;
const DEFAULT_PAGE = 0;

function getPositiveNumberOrFallback(num: number | undefined, fallback: number) {
    if (num === undefined || num < 0) {
        return fallback;

    }
    return num;
}

function calcDefaultStart(start?: number): number {
    return getPositiveNumberOrFallback(start, DEFAULT_PAGE);
}

function calcDefaultPageSize(pageSize?: number): number {
    return getPositiveNumberOrFallback(pageSize, DEFAULT_PAGE_SIZE);
}

function calcDefaultSearchParams(params?: string): string {
    return params ?? "";
}

export interface PaginatedData<T> {
    data: {
        value?: T[];
        currentPage: number;
        pageCount: number;
        isLoading: boolean;
        error: any;
    },
    setPage: (_: number) => void;
    setSearchString: (_: string) => void;
    refetch: () => void;
    searchParams: URLSearchParams;
    opts: QueryOptions;
}

export interface UsePaginatedDataOptions {
    page: number;
    pageSize?: number;
    searchString?: string;
}

export interface PaginationResponse<T> {
  data: T[];
  meta: PaginationMetaData;
  links: PaginationLinks;
}

export type PaginationMetaData = {
  page: number;
  pageSize: number;
  totalPages: number;
  totalItems: number;
}

export interface PaginationLinks {
  self: string;
  first: string;
  next: string;
  prev: string;
  last: string;
}

// The same as AbortableCallbackWithArgs<RefetchResponse<T>, QueryOptions> but easier to read.
export type PaginatedDataFetchFunction<T> = (_signal: AbortSignal, _args: QueryOptions) => Promise<PaginationResponse<T>>

export function usePaginatedData<T>(refetchFunction: PaginatedDataFetchFunction<T>, options?: UsePaginatedDataOptions): PaginatedData<T> {
    const [searchParams, setSearchParams] = useSearchParams();
    const defaultPage = calcDefaultStart(options?.page);
    const defaultPageSize = calcDefaultPageSize(options?.pageSize);
    const defaultSearchString = calcDefaultSearchParams(options?.searchString);
    const [opts, setOpts] = useState<QueryOptions>({
        page: defaultPage,
        page_size: defaultPageSize,
        query: defaultSearchString,
    });
    const page = Number(searchParams.get(PAGE_QUERY_PARAM) ?? defaultPage);
    const searchQuery = searchParams.get(SEARCH_QUERY_PARAM) ?? defaultSearchString;

    const {data, isLoading, error} = useAPI<PaginationResponse<T>, QueryOptions>(refetchFunction as any, opts);

    const setPage = function (page: number) {
        setSearchParams(current => {
            current.set(PAGE_QUERY_PARAM, `${page}`);
            return current;
        });
    };

    const setSearchString = function (search: string) {
        setSearchParams(current => {
            current.set(SEARCH_QUERY_PARAM, `${search}`);
            current.set(PAGE_QUERY_PARAM, `${1}`);
            return current;
        });
    };

    const refetch = async function () {
        setOpts({...opts});
    };

    const pageCount = data?.meta.pageSize ?? DEFAULT_PAGE_SIZE;
    const currentPage = data?.meta.page ?? DEFAULT_PAGE;

    useEffect(() => {
        if (opts.page !== page - 1 || opts.query !== searchQuery) {
            setOpts({...opts, page: page, query: searchQuery});
        }
    }, [searchParams]);

    useEffect(() => {
        if (data !== undefined && page > currentPage || page < 1) {
            setPage(currentPage);
        }
    });

    return {
        data: {
            value: data?.data,
            error: error,
            isLoading: isLoading,
            pageCount: pageCount,
            currentPage: currentPage,
        },
        setPage: setPage,
        setSearchString: setSearchString,
        refetch: refetch,
        searchParams: searchParams,
        opts: opts,
    };
}
