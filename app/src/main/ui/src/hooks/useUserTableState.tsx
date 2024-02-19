import {useState} from "react";
import {type User, UsersService} from "../services/Users";
import {PaginationState} from "@cloudogu/ces-theme-tailwind";
import {SEARCH_QUERY_PARAM} from "./usePaginatedData";
import useSearchParamState from "./useSearchParamState";
import useUrlPaginationControl from "./useUrlPaginationControl";

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
    const [allLineCount, setAllLineCount] = useState(0);
    const [isLoading, setIsLoading] = useState(true);
    const [searchQuery, setSearchQuery] = useSearchParamState(SEARCH_QUERY_PARAM, "");

    const {paginationControl} = useUrlPaginationControl({
        defaultStartPage: DEFAULT_START_PAGE,
        defaultLinesPerPage: DEFAULT_LINES_PER_PAGE,
        pageQueryParam: "p",
        linesPerPageQueryParam: "l",
        lineCountOptions: LINE_COUNT_OPTIONS,
        allLineCount: allLineCount,
        loadDataFunction: async (paginationState: PaginationState) => {
            console.log(`i will load users now for page ${paginationState.page} with lpp=${paginationState.linesPerPage}`);
            // paginationControl.setLinesPerPage(paginationState.linesPerPage);
            // paginationControl.setPage(paginationState.page);
        },
    });


    // useEffect(() => {
    //     (async () => {
    //         if (synchronized) {
    //             console.log("BOOOOOM");
    //             try {
    //                 setIsLoading(true);
    //                 const newUsers = await UsersService.find(undefined, {
    //                     start: Math.max(paginationControl.currentStart - 1, 0), // Backend expects index 0, but start stats at 1
    //                     query: searchQuery,
    //                     exclude: [],
    //                     limit: paginationControl.linesPerPage,
    //                 });
    //                 setUsers(newUsers.data);
    //                 setAllLineCount(newUsers.pagination.totalEntries);
    //             } finally {
    //                 setIsLoading(false);
    //             }
    //         }
    //     })()
    // }, [synchronized, paginationControl]);

    return {
        users,
        isLoading: isLoading,
        paginationControl: paginationControl,
        onDelete: async (name: string) => {
            return UsersService
                .delete(name)
                .finally();
        },
        searchQuery: searchQuery,
        updateSearchQuery: setSearchQuery,
    };
    // const [users, setUsers] = useState<User[]>([]);
    // const [linesPerPage, setLinesPerPage] = useSearchParamState(LINES_PER_PAGE_QUERY_PARAM, `${25}`);
    // const [page, setPage] = useSearchParamState(PAGE_QUERY_PARAM, `${1}`);
    // const [searchQuery, setSearchQuery] = useSearchParamState(SEARCH_QUERY_PARAM, "");
    // const [start, setStart] = useState(Number(linesPerPage) * Number(page) - Number(linesPerPage));
    // const [totalEntries, setTotalEntries] = useState(0);
    // const [isLoading, setIsLoading] = useState(true);

    // const refetchUsers = useCallback(async () => {
    //     const newUsers = await UsersService.find(undefined, {
    //         start: start,
    //         limit: limit,
    //         exclude: [],
    //         query: searchQuery,
    //     });
    //
    //     setUsers(newUsers.data);
    //     setTotalEntries(newUsers.pagination.totalEntries);
    // }, [searchQuery, start, limit]);
    //
    // useEffect(() => {
    //     refetchUsers().finally(() => setIsLoading(false));
    // }, [searchQuery, start, limit]);

    // const paginationControl = usePaginationControl({
    //     defaultStartPage: Number(/*page*/1),
    //     defaultLinesPerPage: Number(linesPerPage),
    //     allLineCount: totalEntries,
    //     lineCountOptions: LINE_COUNT_OPTIONS,
    //     loadDataFunction: async (paginationState: PaginationState) => {
    //         // setStart(Math.max(paginationState.currentStart - 1, 0)); // Pagination starts at 1, backend expects index 0
    //         setLinesPerPage(`${paginationState.linesPerPage}`);
    //     },
    // });
    //
    // useEffect(() => {
    //     // if (page !== `${paginationControl.page}`) {
    //     //     setPage(`${paginationControl.page}`);
    //     // }
    //     if (!LINE_COUNT_OPTIONS.includes(Number(linesPerPage))) {
    //         setLinesPerPage(`${25}`)
    //     }
    // });
    //
    // useEffect(() => {
    //     if (/*page === `${paginationControl.page}` &&*/ LINE_COUNT_OPTIONS.includes(Number(linesPerPage))) {
    //         console.log("DONE");
    //     } else {
    //         console.log("not done");
    //     }
    // });
    //
    // return {
    //     users: users,
    //     isLoading: isLoading,
    //     paginationControl: paginationControl,
    //     updateSearchQuery: undefined as any, //setSearchQuery,
    //     searchQuery: undefined as any, //searchQuery,
    //     onDelete: async (username: string) => {
    //         return UsersService.delete(username).finally(/*TODO*/);
    //     },
    // };
}
