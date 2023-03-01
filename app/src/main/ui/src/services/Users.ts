import {createPaginationData, defaultPaginationData, PagedModel} from "../lib/pagination";
import {UsersAPI} from "../api/UsersAPI";
import {QueryOptions} from "../hooks/useAPI";

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
    async get(signal?: AbortSignal, opts?: QueryOptions): Promise<UsersModel> {
        return new Promise<UsersModel>(async (resolve, reject) => {
            try {
                const usersData = await UsersAPI.get(signal, opts);
                const paginationModel = createPaginationData(usersData.start, usersData.limit, usersData.totalEntries)
                let model: UsersModel = {users: usersData.entries, pagination: paginationModel}
                resolve(model);
            } catch (e) {
                reject(e);
            }
        })
    },
    async delete(userName: string): Promise<void> {
        return new Promise<void>(async (resolve, reject) => {
            try {
                resolve(UsersAPI.delete(userName));
            } catch (e) {
                reject(e);
            }
        })
    }
}