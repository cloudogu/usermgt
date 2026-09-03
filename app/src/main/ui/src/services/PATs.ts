import {Axios} from "../api/axios";
import {isSuccessStatus} from "../helpers/api";

export type PATMetadata = {
    id: string;
    userId: string;
    displayName: string;
    createdAt: string;
    expiresAt: string | null;
    scope: string;
};

export const PATService = {
    async getAll(signal?: AbortSignal): Promise<PATMetadata[]> {
        const response = await Axios.get<PATMetadata[]>("/pats", {signal});
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to load personal access tokens: " + response.status);
        }
        return response.data;
    },
};
