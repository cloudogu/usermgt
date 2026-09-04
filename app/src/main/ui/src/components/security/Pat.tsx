import {
    CesIconSpinner, Label
} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {t} from "../../helpers/i18nHelpers";
import PatList from "./PatList";
import type {PAT} from "../../hooks/usePAT";

export type PTAManagementProps = {
    pat: PAT;
    patError?: Error;
    isPATLoading: boolean;
};

export function PTAManagement({pat, patError, isPATLoading}: PTAManagementProps) {
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
        <>
            <h2>{t("security.pta.title")}</h2>
            <Label text={t("security.pta.title.discription")}/>
            <hr className="my-4 border-0 border-t border-neutral-300" />
            <h3>{t("security.pta.headline")}</h3>
            <PatList tokens={pat.tokens}/>
        </>
    );
}

export default PTAManagement;
