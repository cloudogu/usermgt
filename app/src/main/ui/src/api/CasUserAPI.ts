import {Axios} from "./axios";


export type ApiUser = {
    principal: string;
}

export const CasUserAPI = {
    get: async () : Promise<ApiUser> => {
        return new Promise<ApiUser>(async (resolve, reject) => {
            try {
                const response = await Axios<ApiUser>("/subject");
                resolve(response.data);
            } catch (e) {
                reject(e);
            }
        })
    },
}