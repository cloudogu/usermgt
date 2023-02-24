import {GroupsModel} from "../services/Groups";
import {QueryOptions, StateSetter, useAPI} from "./useAPI";
import {GroupsService} from "../services/Groups";

export const useGroups = (opts: QueryOptions): [GroupsModel | undefined, boolean, StateSetter<GroupsModel>] => {
    const [groups, isLoading, setGroups] = useAPI<GroupsModel>(GroupsService.get, opts)
    return [groups, isLoading, setGroups]
}
