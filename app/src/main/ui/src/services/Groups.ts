import {QueryOptions} from "../hooks/useAPI";
import {createPaginationData, PagedModel} from "../lib/pagination";
import {Axios} from "../api/axios";

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
    isSystemGroup: boolean;
}

export type UndeletableGroupsResponse = string[];

export const GroupsService = {
    async get(signal?: AbortSignal, opts?: QueryOptions): Promise<GroupsModel> {
        return new Promise<GroupsModel>(async (resolve, reject) => {
            try {
                const groupsResponse = await Axios.get<GroupsResponse>("/groups", {
                    params: opts,
                    signal: signal
                });
                if (groupsResponse.status < 200 || groupsResponse.status > 299) {
                    reject(new Error("failed to load group data: " + groupsResponse.status));
                }
                const undeletableGroupsResponse = await Axios<UndeletableGroupsResponse>("/groups/undeletable", {
                    signal: signal
                });
                if (undeletableGroupsResponse.status < 200 || undeletableGroupsResponse.status > 299) {
                    reject(new Error("failed to load undeletable groups information: " + undeletableGroupsResponse.status));
                }
                const groupsData = groupsResponse.data;
                const undeletableGroupsData = undeletableGroupsResponse.data;
                const groups = mapSystemGroups(groupsData.entries, undeletableGroupsData);
                const paginationModel = createPaginationData(groupsData.start, groupsData.limit, groupsData.totalEntries)
                let model: GroupsModel = {groups: groups, pagination: paginationModel}
                resolve(model);
            } catch (e) {
                reject(e);
            }
        })
    },
    async delete(groupName: string): Promise<void> {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const response = await Axios.delete(`/groups/${groupName}`);
                if (response.status !== 204) {
                    reject(new Error(`failed to delete group '${groupName}': ${response.status}`))
                }
                resolve();
            } catch (e) {
                reject(e);
            }
        });
    }
}

const mapSystemGroups = (groups: Group[], undeletableGroups: UndeletableGroupsResponse): Group[] => {
    return groups.map(grp => {
        if (undeletableGroups.includes(grp.name)) {
            grp.isSystemGroup = true;
        }
        return grp
    });
}