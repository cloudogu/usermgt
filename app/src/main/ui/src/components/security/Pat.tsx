import {
    CesIconSpinner, Label
} from "@cloudogu/ces-theme-tailwind";
import React, {useState} from "react";
import {t} from "../../helpers/i18nHelpers";
import Badge from "../Badge";
import PatList from "./PatList";
import type {PAT} from "../../hooks/usePAT";

export type PATManagementProps = {
    pat: PAT;
    patError?: Error;
    isPATLoading: boolean;
};

export function PATManagement({pat, patError, isPATLoading}: PATManagementProps) {
    const [deletedTokenIds, setDeletedTokenIds] = useState<string[]>([]);
    const tokens = pat.tokens.filter(token => !deletedTokenIds.includes(token.id));
    if (isPATLoading) {
        return  <div className="flex min-h-[60vh] items-center justify-center">
                    <CesIconSpinner
                        role="status"
                        aria-label={t("security.overview.title")}
                        className="h-16 w-16 animate-spin"
                    />
                </div>
    }

    if (patError) {
        return <p className="my-4 text-danger" role="alert">
            {t("security.pta.load.error")}
        </p>;
    }

    const tokenCount = String(tokens.length);

    return (
        <>
            <div className="inline-flex items-center gap-3">
                <h3 id={"pat-header"}>{t("security.overview.headline")}</h3>
                <span data-testid="security-pat-count" className="mb-2"><Badge text={tokenCount} className={"px-4"}/></span>
            </div>
            <PatList
                tokens={tokens}
                onTokenDeleted={id => setDeletedTokenIds(current => [...current, id])}
                labelledBy={"pat-header"}
            />
        </>
    );
}

export default PATManagement;
