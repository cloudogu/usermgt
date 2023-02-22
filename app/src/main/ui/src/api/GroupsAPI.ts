import {Axios} from "./axios";
import {QueryOptions} from "../hooks/useAPI";

export interface Group {
    name: string;
    description: string;
    members: string[];
    isSystemGroup: boolean;
}

interface GroupsResponse {
    entries: Group[];
}

export type UndeletableGroups = string[];

export const GroupsAPI = {
    getAll: async (opts?: QueryOptions): Promise<Group[]> => {
        return new Promise<Group[]>(async (resolve, reject) => {
            try {
                const groupsResponse = await Axios.get<GroupsResponse>("/groups", {
                    params: opts
                });
                const undeletableGroups = await Axios<UndeletableGroups>("/groups/undeletable");
                resolve(mapSystemGroups(groupsResponse.data.entries, undeletableGroups.data));
            } catch (e) {
                reject(e);
            }
        })
    }
}

const mapSystemGroups = (groups: Group[], undeletableGroups: UndeletableGroups): Group[] =>{
     return groups.map(grp => {
        if (undeletableGroups.includes(grp.name)) {
            grp.isSystemGroup = true;
        }
        return grp
    });
}
