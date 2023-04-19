import {GroupsService} from "../services/Groups";
import {useAPI} from "./useAPI";
import type {Group} from "../services/Groups";

export const useGroup = (groupName?: string) => {
    if (!groupName) {
        return {name: "", description: "", members: []};
    }
    const {data:group, setData:setGroup, isLoading, error} = useAPI<Group, string>(GroupsService.get, groupName);
    return {group, setGroup, isLoading, error};
};
