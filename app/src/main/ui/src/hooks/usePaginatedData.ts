
import {useEffect, useState} from "react";
import {useSearchParams} from "react-router-dom";
import {useAPI} from "./useAPI";
import type {QueryOptions, AbortableCallbackWithArgs} from "./useAPI";

const PAGE_QUERY_PARAM = "p";
const SEARCH_QUERY_PARAM = "q";
const DEFAULT_PAGE_SIZE = 20;
const DEFAULT_START = 1;

function getPositiveNumberOrFallback(num: number | undefined, fallback: number) {
    if (num === undefined || num <= 0) {
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

export interface RefetchResponse<T> {
    data: T;
    pagination: {
        start: number;
        limit: number;
        totalEntries: number;
    };
}

export function usePaginatedData<T>(refetchFunction: AbortableCallbackWithArgs<RefetchResponse<T>, QueryOptions>, options?: UsePaginatedDataOptions): PaginatedData<T> {
    const [searchParams, setSearchParams] = useSearchParams();
    const defaultStart = calcDefaultStart(options?.start);
    const defaultPageSize = calcDefaultPageSize(options?.pageSize);
    const defaultSearchString = calcDefaultSearchParams(options?.searchString);
    const [opts, setOpts] = useState<QueryOptions>({
        start: defaultStart,
        limit: defaultPageSize,
        query: defaultSearchString
    });
    const page = Number(searchParams.get(PAGE_QUERY_PARAM) ?? defaultStart);
    const searchQuery = searchParams.get(SEARCH_QUERY_PARAM) ?? defaultSearchString;

    const {data, isLoading, error} = useAPI<RefetchResponse<T>, QueryOptions>(refetchFunction, opts);

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

    useEffect(() => {
        if (opts.start !== page) {
            setOpts({...opts, start: page, query: searchQuery});
        }
    }, [searchParams]);

    return {
        data: {
            value: data?.data,
            error: error,
            isLoading: isLoading,
            pageCount: Math.ceil((data?.pagination?.totalEntries ?? defaultPageSize) / defaultPageSize),
            currentPage: page,
        },
        setPage: setPage,
        setSearchString: setSearchString,
        refetch: refetch,
        searchParams: searchParams,
        opts: opts,
    };
}