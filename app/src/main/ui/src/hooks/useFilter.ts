import {useEffect, useState} from "react";
import {DEFAULT_QUERY_LIMIT, QueryOptions, StateSetter} from "./useAPI";

export function useFilter(): [StateSetter<string>, StateSetter<number>, QueryOptions] {
    const [query, setQuery] = useState<string>();
    const [opts, setOpts] = useState<QueryOptions>(new QueryOptions(undefined, DEFAULT_QUERY_LIMIT, query));
    const [page, setPage] = useState<number|undefined>(1)

    useEffect(() => {
        console.log(`update page (${page}) | query (${query})`);
        const newStartValue = page === 1 ? 0 : DEFAULT_QUERY_LIMIT * ((page ?? 2) - 1)
        setOpts(new QueryOptions(newStartValue, DEFAULT_QUERY_LIMIT, query));
    }, [query, page]);

    return [setQuery, setPage, opts]
}