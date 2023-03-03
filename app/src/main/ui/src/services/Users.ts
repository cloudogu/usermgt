import {Axios} from "../api/axios";
import {createPaginationData, defaultPaginationData} from "../lib/pagination";
import type {QueryOptions} from "../hooks/useAPI";
import type { PagedModel} from "../lib/pagination";

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

export const DefaultUsersModel: UsersModel = {users: [], pagination: defaultPaginationData};

export const UsersService = {
    async get(signal?: AbortSignal,opts?: QueryOptions): Promise<UsersModel> {
        const usersResponse = await Axios.get<UsersResponse>("/users", {
            params: opts,
            signal: signal
        });
        if(usersResponse.status < 200 || usersResponse.status > 299) {
            throw new Error("failed to load user data: " + usersResponse.status);
        }
        const usersData = usersResponse.data;
        const paginationModel = createPaginationData(usersData.start, usersData.limit, usersData.totalEntries);
        return {users: usersData.entries, pagination: paginationModel};
    },
    async delete(userName: string): Promise<void> {
        const usersResponse = await Axios.delete<UsersResponse>(`/users/${userName}`);
        if(usersResponse.status !== 204) {
            throw new Error("failed to delete user: " + usersResponse.status);
        }
    }
};
