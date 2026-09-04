import {
    ActionTableFrontendPaginated,
    ActionTableRoot, CesIconArrowDown, CesIconArrowUp,
    usePaginationControl,
} from "@cloudogu/ces-theme-tailwind";
import i18n from "i18next";
import React, {useMemo, useState} from "react";
import StatusIndicator from "../../helpers/StatusIndicator";
import {t} from "../../helpers/i18nHelpers";
import type {PersonalAccessToken} from "../../hooks/usePAT";
import "./PatList.css";

export type PatListProps = {
    tokens: PersonalAccessToken[];
};

type SortableColumn = "displayName" | "status" | "createdAt" | "expiresAt";
type SortDirection = "ascending" | "descending";

function formatDate(value: string): string {
    if (!value || value === "-") {
        return t("pta.table.date.never");
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return t("pta.table.date.never");
    }

    return new Intl.DateTimeFormat(i18n.language, {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
    }).format(date);
}

export function PatList({tokens}: PatListProps) {
    const [sortColumn, setSortColumn] = useState<SortableColumn>("displayName");
    const [sortDirection, setSortDirection] = useState<SortDirection>("ascending");

    const sortedTokens = useMemo(() => [...tokens].sort((left, right) => {
        const comparison = left[sortColumn].localeCompare(right[sortColumn], undefined, {
            numeric: true,
            sensitivity: "base",
        });

        return sortDirection === "ascending" ? comparison : -comparison;
    }), [tokens, sortColumn, sortDirection]);

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
                                {sortableHeader("displayName", t("security.pta.table.displayName"))}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                {sortableHeader("status", t("security.pta.table.status"))}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                {sortableHeader("createdAt", t("security.pta.table.createdAt"))}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                {sortableHeader("expiresAt", t("security.pta.table.expiresAt"))}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                            <ActionTableFrontendPaginated.HeadWithOneRow.Column align="center">
                                {t("security.pta.table.action")}
                            </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                        </ActionTableFrontendPaginated.HeadWithOneRow>
                        <ActionTableFrontendPaginated.Body>
                            {paginatedTokens.map(token => (
                                <ActionTableFrontendPaginated.Body.Row
                                    key={token.id}
                                    data-testid={`personal-access-token-row-${token.id}`}
                                >
                                    <ActionTableFrontendPaginated.Body.Row.Column className="break-all">
                                        {token.displayName}
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
                                        -
                                    </ActionTableFrontendPaginated.Body.Row.Column>
                                </ActionTableFrontendPaginated.Body.Row>
                            ))}
                        </ActionTableFrontendPaginated.Body>
                    </>
                )}
            </ActionTableFrontendPaginated>
        </ActionTableRoot>
    );
}

export default PatList;
