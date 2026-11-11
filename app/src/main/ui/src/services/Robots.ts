import {Axios} from "../api/axios";
import {isSuccessStatus} from "../helpers/api";
import type {CreatePATRequest, CreatePATResponse, PATMetadata} from "./PATs";

export const RobotsService = {
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
    async renew(username: string, expiredAt?: string | null): Promise<CreatePATResponse> {
        const response = await Axios.put<CreatePATResponse>(`/robots/${encodeURIComponent(username)}`, {expiredAt});
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
