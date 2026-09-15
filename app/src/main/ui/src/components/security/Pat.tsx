import {
    CesIconSpinner, Label
} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {t} from "../../helpers/i18nHelpers";
import PatList from "./PatList";
import type {PAT} from "../../hooks/usePAT";
import Badge from "../Badge";

export type PTAManagementProps = {
    pat: PAT;
    patError?: Error;
    isPATLoading: boolean;
};

export function PTAManagement({pat, patError, isPATLoading}: PTAManagementProps) {
    if (isPATLoading) {
        return <CesIconSpinner
            aria-label={t("security.overview.title")}
            className="h-16 w-16 animate-spin text-divider-primary-border"
            role="status"
        />;
    }

    if (patError) {
        return <p className="my-4 text-danger" role="alert">
            {t("security.pta.load.error")}
        </p>;
    }

    const tokenCount = String(pat.tokens.length);

    return (
        <>
            <h2>{t("security.overview.title")}</h2>
            <Label text={t("security.overview.title.discription")}/>
            <hr className="my-4 border-0 border-t border-neutral-300" />
            <span className="inline-flex items-center gap-1.5">
                <h3>{t("security.overview.headline")}</h3>
                <Badge text={tokenCount} className={"mb-2"}/>
            </span>
            <PatList tokens={pat.tokens}/>
        </>
    );
}

export default PTAManagement;
