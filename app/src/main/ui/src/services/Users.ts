import {createPaginationData, defaultPaginationData, PagedModel} from "../lib/pagination";
import {QueryOptions} from "../hooks/useAPI";
import {Axios} from "../api/axios";

export interface UsersResponse {
    entries: User[];
    start: number;
    limit: number;
    totalEntries: number;
}

export type User = {
    username: string;
    displayName: string;
    mail: string;
}

export type UsersModel = PagedModel & {
    users: User[];
}

export const DefaultUsersModel: UsersModel = {users: [], pagination: defaultPaginationData}

export const UsersService = {
    async get(signal?: AbortSignal,opts?: QueryOptions): Promise<UsersModel> {
        return new Promise<UsersModel>(async (resolve, reject) => {
            try {
                const usersResponse = await Axios.get<UsersResponse>("/users", {
                    params: opts,
                    signal: signal
                });
                if(usersResponse.status < 200 || usersResponse.status > 299) {
                    reject(new Error("failed to load user data: " + usersResponse.status))
                }
                const usersData = usersResponse.data;
                const paginationModel = createPaginationData(usersData.start, usersData.limit, usersData.totalEntries)
                let model: UsersModel = {users: usersData.entries, pagination: paginationModel}
                resolve(model)
            } catch (e) {
                reject(e);
            }
        })
    },
    async delete(userName: string): Promise<void> {
        return new Promise<void>(async (resolve, reject) => {
            try {
                const usersResponse = await Axios.delete<UsersResponse>(`/users/${userName}`);
                if(usersResponse.status !== 204) {
                    reject(new Error("failed to delete user: " + usersResponse.status))
                }
                resolve()
            } catch (e) {
                reject(e);
            }
        })
    }
}