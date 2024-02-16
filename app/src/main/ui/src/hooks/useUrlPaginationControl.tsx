import {useEffect} from "react";
import {PaginationControlInput, usePaginationControl} from "@cloudogu/ces-theme-tailwind";
import useNumberSearchParamState from "./useNumberSearchParamState";

export type UrlPaginationControlInput = PaginationControlInput & {
    pageQueryParam?: string,
    linesPerPageQueryParam?: string
};
export default function useUrlPaginationControl(
    {
        pageQueryParam = "p",
        linesPerPageQueryParam = "l",
        ...options
    }: UrlPaginationControlInput
) {
    const defaultLinesPerPage = options.defaultLinesPerPage ?? 1;
    const defaultLineCountOptions = options.lineCountOptions ?? [1];
    const {
        state: page,
        setState: setPage,
        synchronized: s1
    } = useNumberSearchParamState(pageQueryParam, options.defaultStartPage ?? 1);
    const {
        state: linesPerPage,
        setState: setLinesPerPage,
        synchronized: s2
    } = useNumberSearchParamState(linesPerPageQueryParam, defaultLinesPerPage);

    const paginationControl = usePaginationControl(options);

    useEffect(() => {
        if (page !== paginationControl.page) {
            setPage(paginationControl.page);
        }
    }, [page, paginationControl.page]);

    useEffect(() => {
        if (!defaultLineCountOptions.includes(linesPerPage)) {
            setLinesPerPage(defaultLinesPerPage);
        }
    }, [linesPerPage]);

    return {
        paginationControl: {...paginationControl, setPage: setPage, setLinesPerPage: setLinesPerPage},
        synchronized: s1 && s2 && page === paginationControl.page && defaultLineCountOptions.includes(linesPerPage),
    };
}
