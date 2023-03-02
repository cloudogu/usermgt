import {useEffect, useState} from "react";
import {QueryOptions} from "./useAPI";
import {DEFAULT_PAGE_SIZE, calcPageStart} from "../lib/pagination";
import {useSearchParams} from "react-router-dom";

const PAGE_QUERY_PARAM = "p";
const SEARCH_QUERY_PARAM = "q"

export function useFilter(): [(_: string) => void, (_: number) => void, () => void, QueryOptions] {
    const [searchParams, setSearchParams] = useSearchParams();
    const [page, query] = [+(searchParams.get(PAGE_QUERY_PARAM) ?? 1), searchParams.get(SEARCH_QUERY_PARAM) ?? ""]
    const [update, setUpdate] = useState<boolean>(false)
    const refetch = () => setUpdate(!update);
    const [opts, setOpts] = useState<QueryOptions>(new QueryOptions(0, DEFAULT_PAGE_SIZE, ""));
    const updatePage = (newPage: number): void => {
        setSearchParams(current => {
            current.set(PAGE_QUERY_PARAM, `${newPage}`)
            return current;
        })
    };

    const updateQuery = (newQuery: string): void => {
        setSearchParams(current => {
            current.set(SEARCH_QUERY_PARAM, `${newQuery}`)
            current.set(PAGE_QUERY_PARAM, `${1}`)
            return current;
        });
    };

    useEffect(() => {
        const newStart = calcPageStart(page ?? 1);
        setOpts(prevOpts => {
            if(prevOpts.StartValue !== newStart || prevOpts.queryString !== query) {
                return new QueryOptions(newStart, DEFAULT_PAGE_SIZE, query);
            }
            return prevOpts;
        });
    }, [searchParams, update]);

    return [updateQuery, updatePage, refetch, opts]
}
