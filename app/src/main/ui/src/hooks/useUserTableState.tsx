import { useSearchParamState, useUrlPaginationControl} from "@cloudogu/ces-theme-tailwind";
import {useState} from "react";
import {type User, UsersService} from "../services/Users";
import {LINES_PER_PAGE_QUERY_PARAM, PAGE_QUERY_PARAM, SEARCH_QUERY_PARAM} from "./usePaginatedData";
import type {PaginationState} from "@cloudogu/ces-theme-tailwind";

export type UseUsersHook = {
    users: User[],
    isLoading: boolean,
    paginationControl: PaginationState,
    updateSearchQuery: (_: string) => void,
    searchQuery: string,
    onDelete: (_: string) => Promise<void>
};

export const LINE_COUNT_OPTIONS = [
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
    25,
    50,
    100,
];

const DEFAULT_LINES_PER_PAGE = 25;
const DEFAULT_START_PAGE = 1;
export default function useUserTableState(): UseUsersHook {
    const [users, setUsers] = useState<User[]>([]);
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
                const newUsers = await UsersService.find(undefined, {
                    start: Math.max(paginationState.currentStart - 1, 0), // Backend expects index 0, but start stats at 1
                    query: searchQuery,
                    exclude: [],
                    limit: paginationState.linesPerPage,
                });
                setUsers(newUsers.data);
                setAllLineCount(newUsers.pagination.totalEntries);
            } finally {
                setIsLoading(false);
            }
        },
    });

    return {
        users,
        isLoading: isLoading,
        paginationControl: paginationControl,
        onDelete: async (name: string) => UsersService
            .delete(name)
            .finally(),
        searchQuery: searchQuery,
        updateSearchQuery: setSearchQuery,
    };
}
