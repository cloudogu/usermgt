import {
    ActionTableFrontendPaginated,
    ActionTableRoot, CesIconArrowDown, CesIconArrowUp, CesIconTrash,
    usePaginationControl,
} from "@cloudogu/ces-theme-tailwind";

import React, {useMemo, useState} from "react";
import {Link} from "react-router-dom";
import {formatDate, t} from "../../helpers/i18nHelpers";
import {PATService} from "../../services/PATs";
import StatusIndicator from "../StatusIndicator";
import DeletePATDialog from "./DeletePATDialog";
import type {PersonalAccessToken} from "../../hooks/usePAT";
import {DeleteButton} from "../DeleteButton";
import useDoguSelectionStyles from "../../hooks/useDoguSelectionStyles";

export type PatListProps = {
    tokens: PersonalAccessToken[];
    onTokenDeleted: (_id: string) => void;
};

type SortableColumn = "displayName" | "status" | "createdAt" | "expiresAt";
type SortDirection = "ascending" | "descending";

export function PatList({tokens, onTokenDeleted}: PatListProps) {
    const [sortColumn, setSortColumn] = useState<SortableColumn>("displayName");
    const [sortDirection, setSortDirection] = useState<SortDirection>("ascending");
    const [tokenToDelete, setTokenToDelete] = useState<PersonalAccessToken>();

    const sortedTokens = useMemo(() => [...tokens].sort((left, right) => {
        const comparison = left[sortColumn].localeCompare(right[sortColumn], undefined, {
            numeric: true,
            sensitivity: "base",
        });

        return sortDirection === "ascending" ? comparison : -comparison;
    }), [tokens, sortColumn, sortDirection]);

    const deleteToken = async () => {
        if (!tokenToDelete) return;
        await PATService.delete(tokenToDelete.id);
        onTokenDeleted(tokenToDelete.id);
        setTokenToDelete(undefined);
    };

    const changeSorting = (column: SortableColumn) => {
        if (column === sortColumn) {
            setSortDirection(currentDirection => currentDirection === "ascending" ? "descending" : "ascending");
            return;
        }

        setSortColumn(column);
        setSortDirection("ascending");
    };

    const sortableHeader = (column: SortableColumn, label: string) => (
        <button
            type="button"
            className={`flex items-center gap-2 text-left hover:underline ${classes.focusable}`}
            onClick={() => changeSorting(column)}
        >
            <span>{label}</span>
            {sortColumn === column && (
                <span aria-hidden="true">{sortDirection === "ascending" ? <CesIconArrowUp/> : <CesIconArrowDown/>}</span>
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
            <ActionTableFrontendPaginated<PersonalAccessToken>
                values={sortedTokens}
                className="pat-list-table mt-default-2x"
                data-testid="personal-access-tokens"
            >
                {paginatedTokens => (
                    <>
                        <ActionTableFrontendPaginated.HeadWithOneRow>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                {sortableHeader("displayName", t("security.overview.table.displayName"))}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                {sortableHeader("status", t("security.overview.table.status"))}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                {sortableHeader("createdAt", t("security.overview.table.createdAt"))}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                {sortableHeader("expiresAt", t("security.overview.table.expiresAt"))}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column align="center">
                                {t("security.overview.table.action")}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                        </ActionTableFrontendPaginated.HeadWithOneRow>
                        <ActionTableFrontendPaginated.Body>
                            {paginatedTokens.map(token => (
                                <ActionTableFrontendPaginated.Body.Row
                                    key={token.id}
                                    data-testid={`personal-access-token-row-${token.id}`}
                                    className={token.status == "active" ? "" : "bg-neutral-weaker text-neutral"}
                                >
                                    <ActionTableFrontendPaginated.Body.Row.Column className="break-all">
                                        <Link to={`/security/pats/${encodeURIComponent(token.id)}`} className="text-default-text">
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
                                    <ActionTableFrontendPaginated.Body.Row.Column className="text-center">
                                        <DeleteButton
                                            title={t("security.overview.table.action.delete")}
                                            onClick={() => setTokenToDelete(token)}
                                            className={`text-neutral ${classes.focusable}`}
                                        />
                                    </ActionTableFrontendPaginated.Body.Row.Column>
                                </ActionTableFrontendPaginated.Body.Row>
                            ))}
                        </ActionTableFrontendPaginated.Body>
                    </>
                )}
            </ActionTableFrontendPaginated>
            {tokenToDelete && (
                <DeletePATDialog
                    pat={tokenToDelete}
                    onClose={() => setTokenToDelete(undefined)}
                    onConfirm={deleteToken}
                />
            )}
        </ActionTableRoot>
    );
}

export default PatList;
