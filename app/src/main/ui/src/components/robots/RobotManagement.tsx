import {CesIconSpinner} from "@cloudogu/ces-theme-tailwind";
import React, {useState} from "react";
import {t} from "../../helpers/i18nHelpers";
import Badge from "../Badge";
import RobotList from "./RobotList";
import type {RobotsResult} from "../../hooks/useRobots";

export type RobotManagementProps = RobotsResult;

export function RobotManagement({robots, error, isLoading}: RobotManagementProps) {
    const [deletedTokenIds, setDeletedTokenIds] = useState<string[]>([]);
    const tokens = robots.filter(token => !deletedTokenIds.includes(token.id));
    if (isLoading) {
        return  <div className="flex min-h-[60vh] items-center justify-center">
            <CesIconSpinner
                role="status"
                aria-label={t("robots.overview.title")}
                className="h-16 w-16 animate-spin"
            />
        </div>;
    }

    if (error) {
        return <p className="my-4 text-danger" role="alert">
            {t("security.pat.load.error")}
        </p>;
    }

    const tokenCount = String(tokens.length);

    return (
        <>
            <div className="inline-flex items-center gap-3">
                <h3 id={"robot-header"}>{t("robots.overview.headline")}</h3>
                <span data-testid="robots-count" className="mb-2">
                    <Badge text={tokenCount} className={"px-4"} ariaText={`${t("robots.overview.table.count")}: `}/>
                </span>
            </div>
            <RobotList
                tokens={tokens}
                onTokenDeleted={id => setDeletedTokenIds(current => [...current, id])}
                labelledBy={"robot-header"}
            />
        </>
    );
}

export default RobotManagement;
