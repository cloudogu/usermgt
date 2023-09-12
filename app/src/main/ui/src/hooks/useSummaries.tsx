import {ImportUsersService} from "../services/ImportUsers";
import {usePaginatedData} from "./usePaginatedData";
import type {ImportSummary} from "../services/ImportUsers";

export default function useSummaries() {
    return usePaginatedData<ImportSummary[]>(ImportUsersService.listSummaries, {pageSize: 10});
}