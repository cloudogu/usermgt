import {
    ActionTableFrontendPaginated,
    ActionTableRoot, CesIconArrowDown, CesIconArrowUp, CesIconTrash,
    usePaginationControl,
} from "@cloudogu/ces-theme-tailwind";
import i18n from "i18next";
import {Link} from "react-router-dom";
import React, {useMemo, useState} from "react";
import StatusIndicator from "./StatusIndicator";
import DeletePATDialog from "./DeletePATDialog";
import {formatDate, t} from "../../helpers/i18nHelpers";
import type {PersonalAccessToken} from "../../hooks/usePAT";
import {PATService} from "../../services/PATs";
import "./PatList.css";

export type PatListProps = {
    tokens: PersonalAccessToken[];
};

type SortableColumn = "displayName" | "status" | "createdAt" | "expiresAt";
type SortDirection = "ascending" | "descending";

export function PatList({tokens}: PatListProps) {
    const [sortColumn, setSortColumn] = useState<SortableColumn>("displayName");
    const [sortDirection, setSortDirection] = useState<SortDirection>("ascending");
    const [tokenToDelete, setTokenToDelete] = useState<PersonalAccessToken>();
    const [deletedTokenIds, setDeletedTokenIds] = useState<string[]>([]);

    const sortedTokens = useMemo(() => tokens.filter(token => !deletedTokenIds.includes(token.id)).sort((left, right) => {
        const comparison = left[sortColumn].localeCompare(right[sortColumn], undefined, {
            numeric: true,
            sensitivity: "base",
        });

        return sortDirection === "ascending" ? comparison : -comparison;
    }), [tokens, deletedTokenIds, sortColumn, sortDirection]);

    const deleteToken = async () => {
        if (!tokenToDelete) return;
        await PATService.delete(tokenToDelete.id);
        setDeletedTokenIds(current => [...current, tokenToDelete.id]);
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
            className="flex w-full items-center gap-1 text-left"
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
                                        <Link to={`/security/pats/${encodeURIComponent(token.id)}`} className="text-brand underline">
                                            {token.displayName}
                                        </Link>
                                    </ActionTableFrontendPaginated.Body.Row.Column>
                                    <ActionTableFrontendPaginated.Body.Row.Column>
                                        <StatusIndicator text={token.status} />
                                    </ActionTableFrontendPaginated.Body.Row.Column>
                                    <ActionTableFrontendPaginated.Body.Row.Column>
                                        {formatDate(token.createdAt)}
                                    </ActionTableFrontendPaginated.Body.Row.Column>
                                    <ActionTableFrontendPaginated.Body.Row.Column>
                                        {formatDate(token.expiresAt)}
                                    </ActionTableFrontendPaginated.Body.Row.Column>
                                    <ActionTableFrontendPaginated.Body.Row.Column className="text-center">
                                        <button
                                            type="button"
                                            aria-label={t("security.overview.table.action.delete")}
                                            onClick={() => setTokenToDelete(token)}
                                            className="text-neutral w-6 h-6"
                                        >
                                            <CesIconTrash className="text-neutral w-6 h-6"/>
                                        </button>
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
