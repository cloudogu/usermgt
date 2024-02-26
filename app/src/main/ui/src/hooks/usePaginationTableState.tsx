import type {PaginationControl} from "@cloudogu/ces-theme-tailwind";
import {useNumberSearchParamState, useSearchParamState} from "@cloudogu/ces-theme-tailwind";
import {useEffect, useState} from "react";
import type {PaginationResponse} from "./usePaginatedData";
import {LINES_PER_PAGE_QUERY_PARAM, PAGE_QUERY_PARAM, PaginationError, PaginationErrorCode, SEARCH_QUERY_PARAM} from "./usePaginatedData";
import type {QueryOptions} from "./useAPI";

export type UsePaginationHook<T> = {
    items: T[],
    isLoading: boolean,
    paginationControl: PaginationControl,
    updateSearchQuery: (_: string) => void,
    searchQuery: string,
    onDelete: (_: string) => Promise<void>
};

export interface PaginationDataService<T> {
    query (_signal?: AbortSignal, _opts?: QueryOptions): Promise<PaginationResponse<T>>;
    delete (_id: string): Promise<void>;
}

export const LINE_COUNT_OPTIONS = [
    25,
    50,
    100,
];

const DEFAULT_LINES_PER_PAGE = 25;
const DEFAULT_START_PAGE = 1;
export default function usePaginationTableState<T>(dataService: PaginationDataService<T>): UsePaginationHook<T> {
    const [items, setItems] = useState<T[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useSearchParamState(SEARCH_QUERY_PARAM, "");
    const [page, setPage] = useNumberSearchParamState(PAGE_QUERY_PARAM, DEFAULT_START_PAGE);
    const [linesPerPage, setLinesPerPage] = useNumberSearchParamState(LINES_PER_PAGE_QUERY_PARAM, DEFAULT_LINES_PER_PAGE);
    const [maxPage, setMaxPage] = useState(0);
    const [context, setContext] = useState<string | undefined>("");
    const [allLineCount, setAllLineCount] = useState(0);
    const [startItem, setStartItem] = useState(0);
    const [endItem, setEndItem] = useState(0);

    const loadDataFunction = async () => {
        if (!LINE_COUNT_OPTIONS.includes(linesPerPage)){
            return;
        }

        try {
            setIsLoading(true);
            const newItems = await dataService.query(undefined, {
                page: page,
                query: searchQuery,
                exclude: [],
                context: context,
                page_size: linesPerPage,
            });

            setItems(newItems.data);
            setContext(newItems.meta.context);
            setAllLineCount(newItems.meta.totalItems);
            setMaxPage(newItems.meta.totalPages);
            setStartItem(newItems.meta.startItem);
            setEndItem(newItems.meta.endItem);
        } catch (err: any) {
            if (err.name === PaginationErrorCode.ERR_OUT_OF_RANGE) {
                const meta = (err as PaginationError).errorResponse.meta;
                if (meta.page < DEFAULT_START_PAGE) {
                    setPage(DEFAULT_START_PAGE);
                } else {
                    setPage(meta.totalPages);
                }
            }
        }
        finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        loadDataFunction().finally();
    }, [page, linesPerPage, searchQuery]);


    useEffect(() => {
        if (!LINE_COUNT_OPTIONS.includes(linesPerPage)) {
            // Query-param tries to use not allowed value for linesPerPage => reset to default
            setLinesPerPage(DEFAULT_LINES_PER_PAGE);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [linesPerPage]);

    return {
        items,
        isLoading: isLoading,
        paginationControl: {
            page: page,
            setPage,
            allLineCount: allLineCount,
            setLinesPerPage,
            linesPerPage,
            currentStart: startItem,
            currentEnd: endItem,
            defaultLinesPerPage: DEFAULT_LINES_PER_PAGE,
            lineCountOptions: LINE_COUNT_OPTIONS,
            maxPage: maxPage,
            defaultStartPage: 0,
        },
        onDelete: async (name: string) => dataService
            .delete(name)
            .finally(loadDataFunction),
        searchQuery: searchQuery,
        updateSearchQuery: (newValue: string) => {
            setPage(1);
            setSearchQuery(newValue);
        },
    };
}
