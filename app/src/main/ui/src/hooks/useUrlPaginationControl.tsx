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
        defaultStartPage: page,
        lineCountOptions,
        defaultLinesPerPage: linesPerPage
    });

    useEffect(() => {
        if (page !== paginationControl.page) {
            // page in paginationControl has changed and needs to change the query param
            setPage(paginationControl.page);
        }

        if (!lineCountOptions.includes(linesPerPage)) {
            // Query-param tries to use not allowed value for linesPerPage => reset to default
            setLinesPerPage(defaultLinesPerPage);
            paginationControl.setLinesPerPage(defaultLinesPerPage);
        } else if (linesPerPage !== paginationControl.linesPerPage) {
            // linesPerPage in paginationControl has changed and needs to change the query param
            setLinesPerPage(paginationControl.linesPerPage);
        } else {
            // Everything is fine! Trigger load-function
            loadDataFunction?.call(undefined, paginationControl).then();
        }
    }, [linesPerPage, paginationControl.linesPerPage, page, paginationControl.page, paginationControl.allLineCount]);

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
