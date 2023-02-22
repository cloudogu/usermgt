import {Axios} from "./axios";
import {QueryOptions} from "../hooks/useAPI";

export interface User {
    username: string;
    displayName: string;
    mail: string;
}

export interface UsersResponse {
    entries: User[];
}

export const UsersAPI = {
    getAll: async (opts?: QueryOptions): Promise<User[]> => {
        return new Promise<User[]>(async (resolve, reject) => {
            try {
                const usersResponse = await Axios.get<UsersResponse>("/users", {
                    params: opts
                });
                resolve(usersResponse.data.entries)
            } catch (e) {
                reject(e);
            }
        })
    }
}