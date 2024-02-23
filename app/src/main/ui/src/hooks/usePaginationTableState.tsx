import { useSearchParamState, useUrlPaginationControl} from "@cloudogu/ces-theme-tailwind";
import {useState} from "react";
import {LINES_PER_PAGE_QUERY_PARAM, PAGE_QUERY_PARAM, SEARCH_QUERY_PARAM} from "./usePaginatedData";
import type {QueryOptions} from "./useAPI";
import type {PaginationResponse} from "./usePaginatedData";
import type {PaginationState} from "@cloudogu/ces-theme-tailwind";

export type UsePaginationHook<T> = {
    items: T[],
    isLoading: boolean,
    paginationControl: PaginationState,
    updateSearchQuery: (_: string) => void,
    searchQuery: string,
    onDelete: (_: string) => Promise<void>
};

export interface PaginationDataService<T> {
    query (_signal?: AbortSignal, _opts?: QueryOptions): Promise<PaginationResponse<T>>;
    delete (_id: string): Promise<void>;
}

export const LINE_COUNT_OPTIONS = [
    10,
    25,
    50,
    100,
];

const DEFAULT_LINES_PER_PAGE = 25;
const DEFAULT_START_PAGE = 1;
export default function usePaginationTableState<T>(dataService: PaginationDataService<T>): UsePaginationHook<T> {
    const [items, setItems] = useState<T[]>([]);
    // Start at a real high value to prevent the page to reset to 1 at the first request
    const [allLineCount, setAllLineCount] = useState(Number.MAX_SAFE_INTEGER);
    const [isLoading, setIsLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useSearchParamState(SEARCH_QUERY_PARAM, "");

    const loadDataFunction = async (paginationState: PaginationState) => {
        try {
            setIsLoading(true);
            const newItems = await dataService.query(undefined, {
                page: paginationState.page,
                query: searchQuery,
                exclude: [],
                page_size: paginationState.linesPerPage,
            });

            setItems(newItems.data);
            setAllLineCount(newItems.meta.totalItems);
        } finally {
            setIsLoading(false);
        }
    };

    const {paginationControl} = useUrlPaginationControl({
        defaultStartPage: DEFAULT_START_PAGE,
        defaultLinesPerPage: DEFAULT_LINES_PER_PAGE,
        pageQueryParam: PAGE_QUERY_PARAM,
        linesPerPageQueryParam: LINES_PER_PAGE_QUERY_PARAM,
        lineCountOptions: LINE_COUNT_OPTIONS,
        allLineCount: allLineCount,
        loadDataFunction: loadDataFunction,
    });

    return {
        items,
        isLoading: isLoading,
        paginationControl: paginationControl,
        onDelete: async (name: string) => dataService
            .delete(name)
            // reload data after delete
            .finally(() => loadDataFunction(paginationControl)),
        searchQuery: searchQuery,
        updateSearchQuery: setSearchQuery,
    };
}
