import {useCallback, useEffect, useState} from "react";
import {type User, UsersService} from "../services/Users";
import {PaginationState, usePaginationControl} from "@cloudogu/ces-theme-tailwind";

export type UseUsersHook = {
    users: User[],
    isLoading: boolean,
    paginationControl: PaginationState,
    updateSearchQuery: (_: string) => void,
    searchQuery: string,
    onDelete: (_: string) => Promise<void>
};

export const LINE_COUNT_OPTIONS = [
    25,
    50,
    100,
];

export default function useUserTableState({initialSearchQuery, defaultStartPage, defaultLinesPerPage}: {
    initialSearchQuery: string;
    defaultLinesPerPage: number;
    defaultStartPage: number;
}): UseUsersHook {
    const [searchQuery, setSearchQuery] = useState(initialSearchQuery);
    const [users, setUsers] = useState<User[]>([]);
    const [start, setStart] = useState(defaultStartPage);
    const [limit, setLimit] = useState(defaultLinesPerPage);
    const [totalEntries, setTotalEntries] = useState(0);
    const [isLoading, setIsLoading] = useState(true);
    const refetchUsers = useCallback(async () => {
        const newUsers = await UsersService.find(undefined, {
            start: start,
            limit: limit,
            exclude: [],
            query: searchQuery,
        });

        setUsers(newUsers.data);
        setStart(newUsers.pagination.start);
        setLimit(newUsers.pagination.limit);
        setTotalEntries(newUsers.pagination.totalEntries);
    }, [searchQuery, start, limit]);

    useEffect(() => {
        refetchUsers().finally(() => setIsLoading(false));
    }, [searchQuery, start, limit]);

    const paginationControl = usePaginationControl({
        defaultStartPage: defaultStartPage,
        defaultLinesPerPage: defaultLinesPerPage,
        allLineCount: totalEntries,
        lineCountOptions: LINE_COUNT_OPTIONS,
        loadDataFunction: async (paginationState: PaginationState) => {
            setStart(Math.max(paginationState.currentStart - 1, 0)); // Pagination starts at 1, backend expects index 0
            setLimit(paginationState.linesPerPage);
        },
    });


    return {
        users: users,
        isLoading: isLoading,
        paginationControl: paginationControl,
        updateSearchQuery: setSearchQuery,
        searchQuery: searchQuery,
        onDelete: (username: string) => {
            return UsersService.delete(username).finally(refetchUsers);
        },
    };
}
