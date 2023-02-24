import {Axios} from "./axios";


export type CasUser = {
    principal: string;
}

export const CasUserAPI = {
    get: async () : Promise<CasUser> => {
        return new Promise<CasUser>(async (resolve, reject) => {
            try {
                const response = await Axios<CasUser>("/subject");
                resolve(response.data);
            } catch (e) {
                reject(e);
            }
        })
    },
}