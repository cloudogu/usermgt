import {useMemo} from "react";
import {RobotsService} from "../services/Robots";
import {useAPI} from "./useAPI";
import type {PersonalAccessToken} from "./usePAT";

export type RobotsResult = {
    robots: PersonalAccessToken[];
    isLoading: boolean;
    error: Error | undefined;
};

export function useRobots(): RobotsResult {
    const {data, isLoading, error} = useAPI(RobotsService.getAll);
    const robots = useMemo<PersonalAccessToken[]>(() => (data ?? []).map(pat => ({
        id: pat.id,
        displayName: pat.displayName,
        status: pat.expiresAt && Date.parse(pat.expiresAt) <= Date.now() ? "expired" : "active",
        createdAt: pat.createdAt,
        expiresAt: pat.expiresAt ?? "-",
    })), [data]);

    return {robots, isLoading: isLoading && !error, error};
}
