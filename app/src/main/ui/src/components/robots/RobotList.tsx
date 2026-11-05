import {
    ActionTableFrontendPaginated,
    ActionTableRoot, CesIconArrowDown, CesIconArrowUp,
    usePaginationControl,
} from "@cloudogu/ces-theme-tailwind";

import React, {useMemo, useRef, useState} from "react";
import {Link} from "react-router-dom";
import {formatDate, t, tWithParams} from "../../helpers/i18nHelpers";
import useDoguSelectionStyles from "../../hooks/useDoguSelectionStyles";
import {DeleteButton} from "../DeleteButton";
import {RefreshButton} from "../RefreshButton";
import StatusIndicator from "../StatusIndicator";
import type {RobotsResult} from "../../hooks/useRobots";
import {RobotsService} from "../../services/Robots";
import DeleteRobotDialog from "./DeleteRobotDialog";

type RobotToken = RobotsResult["robots"][number];

enum SortDirection {
    Ascending = "ascending",
    Descending = "descending",
}

export type RobotListProps = {
    tokens: RobotToken[];
    labelledBy?: string;
    onTokenDeleted: (_id: string) => void;
};

type SortableColumn = "displayName" | "status" | "createdAt" | "expiresAt";

export function RobotList({tokens, labelledBy, onTokenDeleted}: RobotListProps) {
    const [sortColumn, setSortColumn] = useState<SortableColumn>("displayName");
    const [sortDirection, setSortDirection] = useState<SortDirection>(SortDirection.Ascending);
    const [tokenToDelete, setTokenToDelete] = useState<RobotToken>();
    const deleteButtonRef = useRef<HTMLButtonElement | null>(null);
    const [announcement, setAnnouncement] = useState("");

    const sortedTokens = useMemo(() => [...tokens].sort((left, right) => {
        const comparison = left[sortColumn].localeCompare(right[sortColumn], undefined, {
            numeric: true,
            sensitivity: "base",
        });

        return sortDirection === SortDirection.Ascending ? comparison : -comparison;
    }), [tokens, sortColumn, sortDirection]);

    const deleteToken = async () => {
        if (!tokenToDelete) return;
        await RobotsService.delete(tokenToDelete.id);
        setAnnouncement(tWithParams("robots.createrobot.modal.delete.succeeded", tokenToDelete.displayName));
        onTokenDeleted(tokenToDelete.id);
        setTokenToDelete(undefined);
    };

    const changeSorting = (column: SortableColumn) => {
        if (column === sortColumn) {
            setSortDirection(currentDirection => currentDirection === SortDirection.Ascending ? SortDirection.Descending : SortDirection.Ascending);
            return;
        }

        setSortColumn(column);
        setSortDirection(SortDirection.Ascending);
    };

    const sortableHeader = (column: SortableColumn, label: string) => (
        <button
            type="button"
            className={`flex items-center gap-2 text-left hover:underline ${classes.focusable}`}
            onClick={() => changeSorting(column)}
        >
            <span>{label}</span>
            {sortColumn === column && (
                <span aria-hidden="true">{sortDirection === SortDirection.Ascending ? <CesIconArrowUp className={"w-6 h-6"}/> : <CesIconArrowDown className={"w-6 h-6"}/>}</span>
            )}
        </button>
    );

    const paginationControl = usePaginationControl({
        lineCountOptions: [25, 50, 100],
        allLineCount: sortedTokens.length,
        defaultStartPage: 1,
        defaultLinesPerPage: 25,
    });

    const classes = useDoguSelectionStyles();

    return (
        <ActionTableRoot paginationControl={paginationControl}>
            <ActionTableFrontendPaginated<RobotToken>
                values={sortedTokens}
                className="pat-list-table mt-default-2x"
                data-testid="robot-tokens"
                aria-labelledby={labelledBy ? labelledBy : ""}
            >
                {paginatedTokens => (
                    <>
                        <ActionTableFrontendPaginated.HeadWithOneRow>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column aria-sort={sortColumn === "displayName" ? sortDirection : undefined} >
                                {sortableHeader("displayName", t("robots.overview.table.displayName"))}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column aria-sort={sortColumn === "status" ? sortDirection : undefined} >
                                {sortableHeader("status", t("robots.overview.table.status"))}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column aria-sort={sortColumn === "createdAt" ? sortDirection : undefined} >
                                {sortableHeader("createdAt", t("robots.overview.table.createdAt"))}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column aria-sort={sortColumn === "expiresAt" ? sortDirection : undefined} >
                                {sortableHeader("expiresAt", t("robots.overview.table.expiresAt"))}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column align="center">
                                {t("robots.overview.table.action")}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                        </ActionTableFrontendPaginated.HeadWithOneRow>
                        <ActionTableFrontendPaginated.Body>
                            {paginatedTokens.map(token => (
                                <ActionTableFrontendPaginated.Body.Row
                                    key={token.id}
                                    data-testid={`robot-token-row-${token.id}`}
                                    className={token.status == "active" ? "" : "bg-neutral-weaker text-neutral"}
                                >
                                    <ActionTableFrontendPaginated.Body.Row.Column className="break-all">
                                        <Link to={`/robots/pats/${encodeURIComponent(token.id)}`} className="text-default-text hover:!text-default-text hover:underline hover:!decoration-current active:!text-brand">
                                            {token.displayName}
                                        </Link>
                                    </ActionTableFrontendPaginated.Body.Row.Column>
                                    <ActionTableFrontendPaginated.Body.Row.Column>
                                        <StatusIndicator text={token.status} variant="secondary"/>
                                    </ActionTableFrontendPaginated.Body.Row.Column>
                                    <ActionTableFrontendPaginated.Body.Row.Column>
                                        {formatDate(token.createdAt)}
                                    </ActionTableFrontendPaginated.Body.Row.Column>
                                    <ActionTableFrontendPaginated.Body.Row.Column>
                                        {formatDate(token.expiresAt)}
                                    </ActionTableFrontendPaginated.Body.Row.Column>
                                    <ActionTableFrontendPaginated.Body.Row.Column>
                                        <RefreshButton
                                            title={t("robots.overview.table.action.renew")}
                                            onClick={event => {
                                                setAnnouncement("");
                                                deleteButtonRef.current = event.currentTarget;
                                                setTokenToDelete(token);
                                            }}
                                            aria-label={`${token.displayName} ${t("robots.overview.table.action.renew")}`}
                                            className={"mr-2"}
                                        />
                                        <DeleteButton
                                            title={t("robots.overview.table.action.delete")}
                                            onClick={event => {
                                                setAnnouncement("");
                                                deleteButtonRef.current = event.currentTarget;
                                                setTokenToDelete(token);
                                            }}
                                            aria-label={`${token.displayName} ${t("robots.overview.table.action.delete")}`}
                                        />
                                    </ActionTableFrontendPaginated.Body.Row.Column>
                                </ActionTableFrontendPaginated.Body.Row>
                            ))}
                        </ActionTableFrontendPaginated.Body>
                    </>
                )}
            </ActionTableFrontendPaginated>
            <div role="status" aria-atomic="true" className="sr-only">
                {announcement}
            </div>
            {tokenToDelete && (
                <DeleteRobotDialog
                    pat={tokenToDelete}
                    returnFocusRef={deleteButtonRef}
                    onClose={() => setTokenToDelete(undefined)}
                    onConfirm={deleteToken}
                />
            )}
        </ActionTableRoot>
    );
}

export default RobotList;
