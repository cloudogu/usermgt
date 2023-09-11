import { GroupsService} from "../services/Groups";


import {usePaginatedData} from "./usePaginatedData";
import type {Group} from "../services/Groups";

export default function useGroups() {
    return usePaginatedData<Group[]>(GroupsService.list, {pageSize: 20, start: 0});
}
