import {Axios} from "./axios";
import {QueryOptions} from "../hooks/useAPI";

export interface Group {
    name: string;
    description: string;
    members: string[];
    isSystemGroup: boolean;
}

export interface GroupsModel {
    groups: Group[];
    currentPage: number;
    maxPages: number;
}

interface GroupsResponse {
    entries: Group[];
    start: number;
    limit: number;
    totalEntries: number;
}

export type UndeletableGroups = string[];

export const GroupsAPI = {
    getAll: async (opts?: QueryOptions): Promise<GroupsModel> => {
        return new Promise<GroupsModel>(async (resolve, reject) => {
            try {
                const groupsResponse = await Axios.get<GroupsResponse>("/groups", {
                    params: opts
                });
                const undeletableGroups = await Axios<UndeletableGroups>("/groups/undeletable");
                const groups = mapSystemGroups(groupsResponse.data.entries, undeletableGroups.data);
                const [curr, max] = getPageInfo(groupsResponse.data.start, groupsResponse.data.limit, groupsResponse.data.totalEntries)
                let model: GroupsModel = {
                    groups: groups,
                    currentPage: curr,
                    maxPages: max
                }
                resolve(model);
            } catch (e) {
                reject(e);
            }
        })
    }
}

const getPageInfo = (start: number, limit: number, all: number): [number, number] => {
    const pages = Math.floor(all / limit) + 1;
    const currentPage = Math.floor(start / limit) + 1
    return [currentPage, pages]
}

const mapSystemGroups = (groups: Group[], undeletableGroups: UndeletableGroups): Group[] =>{
     return groups.map(grp => {
        if (undeletableGroups.includes(grp.name)) {
            grp.isSystemGroup = true;
        }
        return grp
    });
}
