import {Axios} from "../api/axios";

export type CasUser = {
    principal: string;
    admin: boolean;
    loading: boolean;
}

export const CasUserService = {
    get: async (signal?: AbortSignal) : Promise<CasUser> => {
        const response = await Axios<CasUser>("/subject", {
            signal: signal
        });
        return response.data;
    },
};