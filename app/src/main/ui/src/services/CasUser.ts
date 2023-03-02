import {Axios} from "../api/axios";

export type CasUser = {
    principal: string;
    admin: boolean;
}

export const CasUserService = {
    get: async (signal?: AbortSignal) : Promise<CasUser> => {
        return new Promise<CasUser>(async (resolve, reject) => {
            try {
                const response = await Axios<CasUser>("/subject", {
                    signal: signal
                });
                resolve(response.data);
            } catch (e) {
                reject(e);
            }
        })
    },
}