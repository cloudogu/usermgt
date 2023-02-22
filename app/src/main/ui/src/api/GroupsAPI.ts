import {Axios} from "./axios";

export interface Group {
    name: string;
    description: string;
    members: string[];
    isSystemGroup: boolean;
}

type GroupsResponse = {
    entries: Group[];
}

export type UndeletableGroups = string[];

export const GroupsAPI = {
    getAll: async (): Promise<Group[]> => {
        return new Promise<Group[]>(async (resolve, reject) => {
            try {
                const groupsResponse = await Axios<GroupsResponse>("/groups");
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
