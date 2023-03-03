import {GroupsService} from "../services/Groups";
import {useAPI} from "./useAPI";
import type {StateSetter} from "./useAPI";
import type {Group} from "../services/Groups";

export const useGroup = (groupName: string): [Group | undefined, boolean, StateSetter<Group>] => {
    const [group, isLoading, setGroup] = useAPI<Group>(GroupsService.get, groupName);
    return [group, isLoading, setGroup];
};
