import {useEffect, useState} from "react";
import {QueryOptions, StateSetter} from "./useAPI";
import {DEFAULT_PAGE_SIZE, calcPageStart} from "../lib/pagination";

export function useFilter(): [StateSetter<string>, StateSetter<number>, () => void, QueryOptions] {
    const [query, setQuery] = useState<string>();
    const [update, setUpdate] = useState<boolean>(false)
    const refetch = () => setUpdate(!update);
    const [opts, setOpts] = useState<QueryOptions>(new QueryOptions(undefined, DEFAULT_PAGE_SIZE, query));
    const [page, setPage] = useState<number|undefined>(1)

    useEffect(() => {
        setOpts(new QueryOptions(calcPageStart(page ?? 1), DEFAULT_PAGE_SIZE, query));
    }, [query, page, update]);

    return [setQuery, setPage, refetch, opts]
}