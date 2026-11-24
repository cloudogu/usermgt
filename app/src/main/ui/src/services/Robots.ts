import {Axios} from "../api/axios";
import {isSuccessStatus} from "../helpers/api";
import type {CreatePATRequest, CreatePATResponse, PATMetadata} from "./PATs";

type RobotPATMetadata = PATMetadata & {
    owner?: string;
    ownerDisplayName?: string;
    ownerDeleted?: boolean;
    description?: string;
};

type CreateRobotRequest = CreatePATRequest & {owner?: string};
type CreateRobotResponse = CreatePATResponse & RobotPATMetadata;

export type RenewRobotRequest = {
    expiresAt?: string | null;
    owner?: string | null;
    description?: string | null;
};

export const RobotsService = {
    async get(signal?: AbortSignal, identifiers?: {userId?: string; id?: string}): Promise<RobotPATMetadata | undefined> {
        if (!identifiers?.userId || !identifiers.id) {
            throw new Error("user ID and PAT ID are required");
        }
        const response = await Axios.get<RobotPATMetadata>(
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
    async getAll(signal?: AbortSignal): Promise<RobotPATMetadata[]> {
        const response = await Axios.get<RobotPATMetadata[]>("/robots", {signal});
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to load robot personal access tokens: " + response.status);
        }
        return response.data;
    },
    async create(request: CreateRobotRequest): Promise<CreateRobotResponse> {
        const response = await Axios.post<CreateRobotResponse>("/robots", request);
        if (!isSuccessStatus(response.status)) {
            throw new Error("failed to create personal access token: " + response.status);
        }
        return response.data;
    },
    async renew(username: string, request: RenewRobotRequest = {}): Promise<CreateRobotResponse> {
        const response = await Axios.put<CreateRobotResponse>(`/robots/${encodeURIComponent(username)}`, request);
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
