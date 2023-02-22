import {Group, GroupsAPI} from "../api/GroupsAPI";
import {QueryOptions, StateSetter, useAPI} from "./useAPI";


export const useGroups = (opts: QueryOptions): [Group[], boolean, StateSetter<Group[]>] => {
    const [groups, isLoading, setGroups] = useAPI<Group[]>(GroupsAPI.getAll, opts)
    return [groups ?? [], isLoading, setGroups]
}
