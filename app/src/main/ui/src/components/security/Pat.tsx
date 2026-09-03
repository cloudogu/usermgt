import {
    ActionTableFrontendPaginated,
    ActionTableRoot,
    CesIconSpinner,
    Select,
    usePaginationControl,
} from "@cloudogu/ces-theme-tailwind";
import React, {useId, useState} from "react";
import {t} from "../../helpers/i18nHelpers";
import type {PAT, PersonalAccessToken} from "../../hooks/usePAT";

export type PTAManagementProps = {
    pat: PAT;
    patError?: Error;
    isPATLoading: boolean;
};

export function PTAManagement({pat, patError, isPATLoading}: PTAManagementProps) {
    const selectId = useId();
    const [selectedDogu, setSelectedDogu] = useState("");
    const paginationControl = usePaginationControl({
        lineCountOptions: [25, 50, 100],
        allLineCount: pat.tokens.length,
        defaultStartPage: 1,
        defaultLinesPerPage: 25,
    });

    if (isPATLoading) {
        return <CesIconSpinner
            aria-label={t("security.pta.title")}
            className="h-16 w-16 animate-spin text-divider-primary-border"
            role="status"
        />;
    }

    if (patError) {
        return <p className="my-4 text-danger" role="alert">
            {t("security.pta.load.error")}
        </p>;
    }

    return (
        <div className="my-4">
            <label className="mb-1 block font-bold" htmlFor={selectId}>
                Dogu
            </label>
            <Select
                id={selectId}
                data-testid="pta-dogu-select"
                className="w-full"
                placeholder="Dogu auswählen"
                value={selectedDogu}
                onValueChange={setSelectedDogu}
            >
                {pat.dogus.map(dogu => (
                    <Select.Item key={dogu.value} value={dogu.value}>
                        {dogu.label}
                    </Select.Item>
                ))}
            </Select>

            <div className="mt-5 mb-4 flex items-center gap-default-2x">
                <h3 className="desktop:text-desktop-4xl mobile:text-mobile-4xl text-default-text">
                    {t("security.pta.headline")}
                </h3>
                <span className="bg-brand-weaker px-default-2x py-default-1x text-neutral-strong">
                    {pat.tokens.length}
                </span>
            </div>

            <ActionTableRoot paginationControl={paginationControl}>
                <ActionTableFrontendPaginated<PersonalAccessToken>
                    values={pat.tokens}
                    className="mt-default-2x"
                    data-testid="personal-access-tokens"
                >
                    {(tokens: PersonalAccessToken[]) => (
                        <>
                            <ActionTableFrontendPaginated.HeadWithOneRow>
                                <ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                    {t("security.pta.table.displayName")}
                                </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                <ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                    {t("security.pta.table.status")}
                                </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                <ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                    {t("security.pta.table.createdAt")}
                                </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                <ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                    {t("security.pta.table.expiresAt")}
                                </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                <ActionTableFrontendPaginated.HeadWithOneRow.Column align="center">
                                    {t("security.pta.table.action")}
                                </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                            </ActionTableFrontendPaginated.HeadWithOneRow>
                            <ActionTableFrontendPaginated.Body>
                                {tokens.map(token => (
                                    <ActionTableFrontendPaginated.Body.Row
                                        key={token.id}
                                        data-testid={`personal-access-token-row-${token.id}`}
                                    >
                                        <ActionTableFrontendPaginated.Body.Row.Column className="font-bold break-all">
                                            {token.displayName}
                                        </ActionTableFrontendPaginated.Body.Row.Column>
                                        <ActionTableFrontendPaginated.Body.Row.Column>
                                            {token.status}
                                        </ActionTableFrontendPaginated.Body.Row.Column>
                                        <ActionTableFrontendPaginated.Body.Row.Column>
                                            {token.createdAt}
                                        </ActionTableFrontendPaginated.Body.Row.Column>
                                        <ActionTableFrontendPaginated.Body.Row.Column>
                                            {token.expiresAt}
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
        </div>
    );
}

export default PTAManagement;
