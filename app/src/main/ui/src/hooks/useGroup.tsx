import {GroupsService} from "../services/Groups";
import {useAPI} from "./useAPI";
import type {Group} from "../services/Groups";

export const useGroup = (groupName?: string) => {
    if (!groupName) {
        return {name: "", description: "", members: []};
    }
    const {data, setData, isLoading, error} = useAPI<Group, string>(GroupsService.get, groupName);
    return {data, setData, isLoading, error};
};
