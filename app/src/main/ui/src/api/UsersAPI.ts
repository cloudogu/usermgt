import {Axios} from "./axios";
import {QueryOptions} from "../hooks/useAPI";
import {User} from "../services/Users";


export interface UsersResponse {
    entries: User[];
    start: number;
    limit: number;
    totalEntries: number;
}

export const UsersAPI = {
    async get(opts?: QueryOptions): Promise<UsersResponse> {
        return new Promise<UsersResponse>(async (resolve, reject) => {
            try {
                const usersResponse = await Axios.get<UsersResponse>("/users", {
                    params: opts
                });
                if(!usersResponse.data) {
                    reject(new Error("failed to load user data: " + usersResponse.status))
                }
                resolve(usersResponse.data)
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