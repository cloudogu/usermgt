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

export type CreatePATRequest = {
    displayName: string;
    expiresAt?: string;
    scope?: string;
};

export type CreatePATResponse = PATMetadata & {
    token: string;
};

export const PATService = {
    async getAll(signal?: AbortSignal): Promise<PATMetadata[]> {
        const response = await Axios.get<PATMetadata[]>("/pats", {signal});
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to load personal access tokens: " + response.status);
        }
        return response.data;
    },
    async create(request: CreatePATRequest): Promise<CreatePATResponse> {
        const response = await Axios.post<CreatePATResponse>("/pats", request);
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to create personal access token: " + response.status);
        }
        return response.data;
    },
    async delete(id: string): Promise<void> {
        const response = await Axios.delete(`/pats/${encodeURIComponent(id)}`);
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to delete personal access token: " + response.status);
        }
    },
};
