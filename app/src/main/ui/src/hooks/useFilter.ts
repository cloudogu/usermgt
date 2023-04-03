import {calcPageStart, DEFAULT_PAGE_SIZE} from "@cloudogu/ces-theme-tailwind";
import {useEffect, useReducer} from "react";
import {useSearchParams} from "react-router-dom";
import type {QueryOptions} from "./useAPI";

export const PAGE_QUERY_PARAM = "p";
export const SEARCH_QUERY_PARAM = "q";

type QueryOptionsReducerArgs = QueryOptions & {
    force: boolean;
}

function updateQueryOptions(currentState: QueryOptions, newState: QueryOptionsReducerArgs): QueryOptions {
    if (currentState.start !== newState.start || currentState.query !== newState.query || newState.force) {
        return {start: newState.start, limit: newState.limit, query: newState.query};
    }
    return currentState;
}

export function useFilter() {
    const [searchParams, setSearchParams] = useSearchParams();
    const [page, query] = [+(searchParams.get(PAGE_QUERY_PARAM) ?? 1), searchParams.get(SEARCH_QUERY_PARAM) ?? ""];
    const [opts, updateOpts] = useReducer(updateQueryOptions, {start: 0, limit: DEFAULT_PAGE_SIZE, query: ""});
    const refetch = () => updateOpts({...opts, force: true});
    const updatePage = (newPage: number): void => {
        setSearchParams(current => {
            current.set(PAGE_QUERY_PARAM, `${newPage}`);
            return current;
        });
    };

    const updateQuery = (newQuery: string): void => {
        setSearchParams(current => {
            current.set(SEARCH_QUERY_PARAM, `${newQuery}`);
            current.set(PAGE_QUERY_PARAM, `${1}`);
            return current;
        });
    };

    useEffect(() => {
        const newStart = calcPageStart(page ?? 1);
        updateOpts({start: newStart, limit: DEFAULT_PAGE_SIZE, query: query, force: false});
    }, [searchParams]);

    return {updateQuery, updatePage, refetch, searchParams, opts};
}
