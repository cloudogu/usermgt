import {Axios} from "./axios";
import {QueryOptions} from "../hooks/useAPI";
import {Group} from "../services/Groups";


interface GroupsResponse {
    entries: Group[];
    start: number;
    limit: number;
    totalEntries: number;
}

export type UndeletableGroupsResponse = string[];

export const GroupsAPI = {
    async get(opts?: QueryOptions): Promise<GroupsResponse> {
        return new Promise<GroupsResponse>(async (resolve, reject) => {
            try {
                const groupsResponse = await Axios.get<GroupsResponse>("/groups", {
                    params: opts
                });
                if (!groupsResponse.data) {
                    reject(new Error("failed to load group data: " + groupsResponse.status));
                }
                resolve(groupsResponse.data)
            } catch (e) {
                reject(e);
            }
        })
    },
    async undeletable(): Promise<UndeletableGroupsResponse> {
        return new Promise<UndeletableGroupsResponse>(async (resolve, reject) => {
            try {
                const undeletableGroupsResponse = await Axios<UndeletableGroupsResponse>("/groups/undeletable");
                if (!undeletableGroupsResponse.data) {
                    reject(new Error("failed to load undeletable groups information: " + undeletableGroupsResponse.status));
                }
                resolve(undeletableGroupsResponse.data);
            } catch (e) {
                reject(e)
            }
        })

    },
    async delete(groupName: string): Promise<void> {
        return new Promise( async (resolve, reject) => {
            try {
                const response = await Axios.delete(`/groups/${groupName}`);
                if (response.status !== 204) {
                    reject(new Error(`failed to delete group '${groupName}': ${response.status}`))
                }
                resolve()
            } catch (e) {
                reject(e);
            }
        })
    }
}
