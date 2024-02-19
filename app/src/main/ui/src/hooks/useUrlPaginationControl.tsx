import {useCallback, useEffect} from "react";
import {PaginationControlInput, PaginationState, usePaginationControl} from "@cloudogu/ces-theme-tailwind";
import useNumberSearchParamState from "./useNumberSearchParamState";

export type UrlPaginationControlInput = PaginationControlInput & {
    pageQueryParam?: string,
    linesPerPageQueryParam?: string
};
export default function useUrlPaginationControl(
    {
        pageQueryParam = "p",
        linesPerPageQueryParam = "l",
        loadDataFunction,
        defaultStartPage = 1,
        defaultLinesPerPage = 1,
        lineCountOptions = [1],
        ...options
    }: UrlPaginationControlInput
) {
    const [page, setPage] = useNumberSearchParamState(pageQueryParam, defaultStartPage);
    const [linesPerPage, setLinesPerPage] = useNumberSearchParamState(linesPerPageQueryParam, defaultLinesPerPage);

    const paginationControl = usePaginationControl({
        ...options,
        loadDataFunction: loadDataFunction,
        defaultStartPage: page,
        lineCountOptions,
        defaultLinesPerPage: linesPerPage
    });

    useEffect(() => {
        if (page !== paginationControl.page) {
            setPage(paginationControl.page);
            loadDataFunction?.call(undefined, paginationControl);
        }
    }, [page, paginationControl.page]);

    useEffect(() => {
        if (!lineCountOptions.includes(linesPerPage)) {
            setLinesPerPage(defaultLinesPerPage);
        } else if (!lineCountOptions.includes(paginationControl.linesPerPage)) {
            paginationControl.setLinesPerPage(linesPerPage);
        } else if (linesPerPage !== paginationControl.linesPerPage) {
            setLinesPerPage(paginationControl.linesPerPage);
        } else {
            loadDataFunction?.call(undefined, paginationControl).then();
        }
    }, [linesPerPage, paginationControl.linesPerPage]);

    return {
        paginationControl: {
            ...paginationControl,
            page: page,
            linesPerPage: linesPerPage,
            setPage: (v: number) => {
                setPage(v);
                paginationControl.setPage(v);
            },
            setLinesPerPage: (v: number) => {
                setLinesPerPage(v);
                paginationControl.setLinesPerPage(v);
            },
        },
    };
}
