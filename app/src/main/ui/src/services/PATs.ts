import {Axios} from "../api/axios";
import {isSuccessStatus} from "../helpers/api";

export type PATMetadata = {
    id: string;
    userId: string;
    displayName: string;
    createdAt: string;
    expiresAt: string | null;
    scope: string;
    description?: string | null;
};

export type CreatePATRequest = {
    displayName: string;
    description?: string;
    expiresAt?: string;
    scope?: string;
};

export type CreatePATResponse = PATMetadata & {
    token: string;
};

function toPATMetadata({id, userId, displayName, createdAt, expiresAt, scope, description}: PATMetadata): PATMetadata {
    return {id, userId, displayName, createdAt, expiresAt, scope, description};
}

export const PATService = {
    async getAll(signal?: AbortSignal): Promise<PATMetadata[]> {
        const response = await Axios.get<PATMetadata[]>("/pats", {signal});
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to load personal access tokens: " + response.status);
        }
        return response.data.map(toPATMetadata);
    },
    async create(request: CreatePATRequest): Promise<CreatePATResponse> {
        const response = await Axios.post<CreatePATResponse>("/pats", request);
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to create personal access token: " + response.status);
        }
        return {...toPATMetadata(response.data), token: response.data.token};
    },
    async delete(id: string): Promise<void> {
        const response = await Axios.delete(`/pats/${encodeURIComponent(id)}`);
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to delete personal access token: " + response.status);
        }
    },
};
