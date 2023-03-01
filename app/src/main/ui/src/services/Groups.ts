import {QueryOptions} from "../hooks/useAPI";
import {createPaginationData, PagedModel} from "../lib/pagination";
import {GroupsAPI, UndeletableGroupsResponse} from "../api/GroupsAPI";

export type GroupsModel = PagedModel & {
    groups: Group[];
}

export type Group = {
    name: string;
    description: string;
    members: string[];
    isSystemGroup: boolean;
}

export const GroupsService = {
    async get(signal?: AbortSignal, opts?: QueryOptions): Promise<GroupsModel> {
        return new Promise<GroupsModel>(async (resolve, reject) => {
            try {
                const groupsData = await GroupsAPI.get(signal, opts);
                const undeletableGroupsResponse = await GroupsAPI.undeletable(signal);
                const groups = mapSystemGroups(groupsData.entries, undeletableGroupsResponse);
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
                resolve(GroupsAPI.delete(groupName));
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