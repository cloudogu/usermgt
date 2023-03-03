import {Axios} from "../api/axios";
import {t} from "../helpers/i18nHelpers";
import {createPaginationData} from "../lib/pagination";
import type {QueryOptions} from "../hooks/useAPI";
import type {PagedModel} from "../lib/pagination";

export type GroupsModel = PagedModel & {
    groups: Group[];
}

interface GroupsResponse {
    entries: Group[];
    start: number;
    limit: number;
    totalEntries: number;
}

export type Group = {
    name: string;
    description: string;
    members: string[];
    isSystemGroup?: boolean;
}

export type UndeletableGroupsResponse = string[];

export const GroupsService = {
    async list(signal?: AbortSignal, opts?: QueryOptions): Promise<GroupsModel> {
        const groupsResponse = await Axios.get<GroupsResponse>("/groups", {
            params: opts,
            signal: signal
        });
        if (groupsResponse.status < 200 || groupsResponse.status > 299) {
            throw new Error("failed to load group data: " + groupsResponse.status);
        }
        const undeletableGroupsResponse = await Axios<UndeletableGroupsResponse>("/groups/undeletable", {
            signal: signal
        });
        if (undeletableGroupsResponse.status < 200 || undeletableGroupsResponse.status > 299) {
            throw new Error("failed to load undeletable groups information: " + undeletableGroupsResponse.status);
        }
        const groupsData = groupsResponse.data;
        const undeletableGroupsData = undeletableGroupsResponse.data;
        const groups = mapSystemGroups(groupsData.entries, undeletableGroupsData);
        const paginationModel = createPaginationData(groupsData.start, groupsData.limit, groupsData.totalEntries);

        return {groups: groups, pagination: paginationModel};
    },
    async get(signal?: AbortSignal, groupName?: string): Promise<Group> {
        if (!groupName) {
            throw new Error("the group name must not be empty");
        }
        const groupResponse = await Axios.get<Group>(`/groups/${groupName}`, {
            signal: signal
        });
        if (groupResponse.status < 200 || groupResponse.status > 299) {
            throw new Error("failed to load group data: " + groupResponse.status);
        }
        return groupResponse.data;
    },
    async save(group: Group): Promise<void> {
        const response = await Axios.post("/groups", group);
        if (response.status !== 201) {
            throw new Error(t("newGroup.notification.error"));
        }
    },
    async update(group: Group): Promise<void> {
        const response = await Axios.put(`/groups/${group.name}`, group);
        if (response.status !== 204) {
            throw new Error(t("editGroup.notification.error"));
        }
    },
    async delete(groupName: string): Promise<void> {
        const response = await Axios.delete(`/groups/${groupName}`);
        if (response.status !== 204) {
            throw new Error(`failed to delete group '${groupName}': ${response.status}`);
        }
    }
};

const mapSystemGroups = (groups: Group[], undeletableGroups: UndeletableGroupsResponse): Group[] => groups.map(grp => {
    if (undeletableGroups.includes(grp.name)) {
        grp.isSystemGroup = true;
    }
    return grp;
});