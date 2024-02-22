import { useSearchParamState, useUrlPaginationControl} from "@cloudogu/ces-theme-tailwind";
import {useState} from "react";
import {type Group, GroupsService} from "../services/Groups";
import {LINES_PER_PAGE_QUERY_PARAM, PAGE_QUERY_PARAM, SEARCH_QUERY_PARAM} from "./usePaginatedData";
import type {PaginationState} from "@cloudogu/ces-theme-tailwind";

export type UseGroupsHook = {
    groups: Group[],
    isLoading: boolean,
    paginationControl: PaginationState,
    updateSearchQuery: (_: string) => void,
    searchQuery: string,
    onDelete: (_: string) => Promise<void>
};

export const LINE_COUNT_OPTIONS = [
    10,
    25,
    50,
    100,
];

const DEFAULT_LINES_PER_PAGE = 25;
const DEFAULT_START_PAGE = 1;
export default function useGroupTableState(): UseGroupsHook {
    const [groups, setGroups] = useState<Group[]>([]);
    // Start at a real high value to prevent the page to reset to 1 at the first request
    const [allLineCount, setAllLineCount] = useState(Number.MAX_SAFE_INTEGER);
    const [isLoading, setIsLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useSearchParamState(SEARCH_QUERY_PARAM, "");

    const {paginationControl} = useUrlPaginationControl({
        defaultStartPage: DEFAULT_START_PAGE,
        defaultLinesPerPage: DEFAULT_LINES_PER_PAGE,
        pageQueryParam: PAGE_QUERY_PARAM,
        linesPerPageQueryParam: LINES_PER_PAGE_QUERY_PARAM,
        lineCountOptions: LINE_COUNT_OPTIONS,
        allLineCount: allLineCount,
        loadDataFunction: async (paginationState: PaginationState) => {
            try {
                setIsLoading(true);
                const newGroups = await GroupsService.list(undefined, {
                    page: paginationState.page,
                    query: searchQuery,
                    exclude: [],
                    page_size: paginationState.linesPerPage,
                });
                setGroups(newGroups.data);
                setAllLineCount(newGroups.meta.totalItems);
            } finally {
                setIsLoading(false);
            }
        },
    });

    return {
        groups,
        isLoading: isLoading,
        paginationControl: paginationControl,
        onDelete: async (name: string) => GroupsService
            .delete(name)
            .finally(),
        searchQuery: searchQuery,
        updateSearchQuery: setSearchQuery,
    };
}
