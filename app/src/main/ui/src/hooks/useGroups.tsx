import {GroupsService} from "../services/Groups";
import { useAPI} from "./useAPI";
import type {QueryOptions, StateSetter} from "./useAPI";
import type {GroupsModel} from "../services/Groups";

export const useGroups = (opts: QueryOptions): [GroupsModel | undefined, boolean, StateSetter<GroupsModel>] => {
    const [groups, isLoading, setGroups] = useAPI<GroupsModel>(GroupsService.list, opts);
    return [groups, isLoading, setGroups];
};
