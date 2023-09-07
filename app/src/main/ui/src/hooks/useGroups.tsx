import {GroupsService} from "../services/Groups";
import { useAPI} from "./useAPI";
import type {QueryOptions} from "./useAPI";
import type {GroupsModel} from "../services/Groups";

export const useGroups = (opts: QueryOptions):{groups?: GroupsModel, isLoading: boolean} => {
    const {data, isLoading} = useAPI<GroupsModel, QueryOptions>(GroupsService.list, opts);
    return {groups: data, isLoading: isLoading};
};
