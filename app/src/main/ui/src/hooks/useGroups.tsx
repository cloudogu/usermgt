import {GroupsAPI, GroupsModel} from "../api/GroupsAPI";
import {QueryOptions, StateSetter, useAPI} from "./useAPI";


export const useGroups = (opts: QueryOptions): [GroupsModel | undefined, boolean, StateSetter<GroupsModel>] => {
    const [groups, isLoading, setGroups] = useAPI<GroupsModel>(GroupsAPI.getAll, opts)
    return [groups, isLoading, setGroups]
}
