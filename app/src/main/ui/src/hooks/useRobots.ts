import {useMemo} from "react";
import {useTranslation} from "react-i18next";
import {RobotsService} from "../services/Robots";
import {useAPI} from "./useAPI";
import type {PersonalAccessToken} from "./usePAT";

export type RobotToken = PersonalAccessToken & {userId: string; owner: string; ownerDisplayName: string; description: string};

export type RobotsResult = {
    robots: RobotToken[];
    isLoading: boolean;
    error: Error | undefined;
};

export function useRobots(): RobotsResult {
    const {t} = useTranslation();
    const deletedAccountLabel = t("robots.overview.table.owner.deleted");
    const {data, isLoading, error} = useAPI(RobotsService.getAll);
    const robots = useMemo<RobotToken[]>(() => (data ?? []).map(pat => ({
        id: pat.id,
        userId: pat.userId,
        displayName: pat.displayName,
        owner: pat.owner,
        ownerDisplayName: pat.ownerDeleted ? deletedAccountLabel : pat.ownerDisplayName || pat.owner || "",
        description: pat.description,
        status: pat.expiresAt && Date.parse(pat.expiresAt) <= Date.now() ? "expired" : "active",
        createdAt: pat.createdAt,
        expiresAt: pat.expiresAt ?? "-",
    })), [data, deletedAccountLabel]);

    return {robots, isLoading: isLoading && !error, error};
}
