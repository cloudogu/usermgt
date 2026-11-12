import {Axios} from "../api/axios";
import {isSuccessStatus} from "../helpers/api";
import type {CreatePATRequest, CreatePATResponse, PATMetadata} from "./PATs";

export const RobotsService = {
    async get(signal?: AbortSignal, identifiers?: {userId?: string; id?: string}): Promise<PATMetadata | undefined> {
        if (!identifiers?.userId || !identifiers.id) {
            throw new Error("user ID and PAT ID are required");
        }
        const response = await Axios.get<PATMetadata>(
            `/robots/${encodeURIComponent(identifiers.userId)}/${encodeURIComponent(identifiers.id)}`,
            {signal, validateStatus: status => isSuccessStatus(status) || status === 404},
        );
        if (response.status === 404) {
            return undefined;
        }
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to load robot personal access token: " + response.status);
        }
        return response.data;
    },
    async getAll(signal?: AbortSignal): Promise<PATMetadata[]> {
        const response = await Axios.get<PATMetadata[]>("/robots", {signal});
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to load robot personal access tokens: " + response.status);
        }
        return response.data;
    },
    async create(request: CreatePATRequest): Promise<CreatePATResponse> {
        const response = await Axios.post<CreatePATResponse>("/robots", request);
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to create personal access token: " + response.status);
        }
        return response.data;
    },
    async renew(username: string, expiresAt?: string | null): Promise<CreatePATResponse> {
        const response = await Axios.put<CreatePATResponse>(`/robots/${encodeURIComponent(username)}`, {expiresAt});
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to renew personal access token: " + response.status);
        }
        return response.data;
    },
    async delete(username: string): Promise<void> {
        const response = await Axios.delete(`/robots/${encodeURIComponent(username)}`);
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to delete personal access token: " + response.status);
        }
    },
};
