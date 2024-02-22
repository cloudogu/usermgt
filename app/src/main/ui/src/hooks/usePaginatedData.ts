import {useEffect, useState} from "react";
import {useSearchParams} from "react-router-dom";
import {useAPI} from "./useAPI";
import type {QueryOptions} from "./useAPI";

export const PAGE_QUERY_PARAM = "p";
export const SEARCH_QUERY_PARAM = "q";
export const LINES_PER_PAGE_QUERY_PARAM = "l";
const DEFAULT_PAGE_SIZE = 20;
const DEFAULT_START = 0;

function getPositiveNumberOrFallback(num: number | undefined, fallback: number) {
    if (num === undefined || num < 0) {
        return fallback;
    }
    return num;
}

function calcDefaultStart(start?: number): number {
    return getPositiveNumberOrFallback(start, DEFAULT_START);
}

function calcDefaultPageSize(pageSize?: number): number {
    return getPositiveNumberOrFallback(pageSize, DEFAULT_PAGE_SIZE);
}

function calcDefaultSearchParams(params?: string): string {
    return params ?? "";
}

export interface PaginatedData<T> {
    data: {
        value?: T;
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
    start?: number;
    pageSize?: number;
    searchString?: string;
}

export type PaginationResponse = {
    start: number;
    limit: number;
    totalEntries: number;
}

export interface RefetchResponse<T> {
    data: T;
    pagination: PaginationResponse;
}

// The same as AbortableCallbackWithArgs<RefetchResponse<T>, QueryOptions> but easier to read.
export type PaginatedDataFetchFunction<T> = (_signal: AbortSignal, _args: QueryOptions) => Promise<RefetchResponse<T>>

export function usePaginatedData<T>(refetchFunction: PaginatedDataFetchFunction<T>, options?: UsePaginatedDataOptions): PaginatedData<T> {
    const [searchParams, setSearchParams] = useSearchParams();
    const defaultStart = calcDefaultStart(options?.start);
    const defaultPageSize = calcDefaultPageSize(options?.pageSize);
    const defaultSearchString = calcDefaultSearchParams(options?.searchString);
    const [opts, setOpts] = useState<QueryOptions>({
        start: defaultStart,
        limit: defaultPageSize,
        query: defaultSearchString,
    });
    const page = Number(searchParams.get(PAGE_QUERY_PARAM) ?? defaultStart);
    const searchQuery = searchParams.get(SEARCH_QUERY_PARAM) ?? defaultSearchString;

    const {data, isLoading, error} = useAPI<RefetchResponse<T>, QueryOptions>(refetchFunction as any, opts);

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

    const pageCount = Math.ceil((data?.pagination?.totalEntries ?? defaultPageSize) / defaultPageSize);
    const currentPage = Math.max(Math.min(page, pageCount), 1);

    useEffect(() => {
        if (opts.start !== page - 1 || opts.query !== searchQuery) {
            setOpts({...opts, start: Math.max((page - 1) * defaultPageSize, 0), query: searchQuery});
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