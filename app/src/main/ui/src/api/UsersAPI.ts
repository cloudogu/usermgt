import {Axios} from "./axios";

export interface User {
    username: string;
    displayName: string;
    mail: string;
}

export interface UsersResponse {
    entries: User[];
}

export const UsersAPI = {
    getAll: async (): Promise<User[]> => {
        return new Promise<User[]>(async (resolve, reject) => {
            try {
                const usersResponse = await Axios<UsersResponse>("/users");
                resolve(usersResponse.data.entries)
            } catch (e) {
                reject(e);
            }
        })
    }
}