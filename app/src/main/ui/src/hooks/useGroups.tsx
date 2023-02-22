import {Group, GroupsAPI} from "../api/GroupsAPI";
import {useAPI} from "./useAPI";

export const useGroups = (): [Group[], boolean] => {
    const [groups, isLoading] = useAPI<Group[]>(GroupsAPI.getAll)
    return [groups ?? [], isLoading]
}
