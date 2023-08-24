import {Details, H1} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {useLocation} from "react-router-dom";
import UsersImportErrorTable from "../components/usersImport/UsersImportErrorTable";
import UsersImportResultTable from "../components/usersImport/UsersImportResultTable";
import {t} from "../helpers/i18nHelpers";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import type {ImportUsersResponse} from "../services/ImportUsers";
import type {Location} from "history";

const UsersImportResult = (props: { title: string }) => {
    const {state: result} = useLocation() as Location<ImportUsersResponse>;
    useSetPageTitle(props.title);

    const createdRows = result?.created?.length ?? 0;
    const updatedRows = result?.updated?.length ?? 0;
    const failedRows = result?.errors?.length ?? 0;
    const successfulRows = createdRows + updatedRows;
    const affectedRows = successfulRows + failedRows;

    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.usersImportResult")}</H1>
        </div>
        {!result &&
            <div>
                ERROR
            </div>
        }
        {result && affectedRows > 0 &&
            <div>
                <p className={"mb-4 mt-2"}>
                    {failedRows === 0 && t("usersImportResult.result.success")}
                    {failedRows > 0 && t("usersImportResult.result.successWithFailures")}
                    {successfulRows === 0 && t("usersImportResult.result.failure")}
                </p>
                <Details hidden={createdRows === 0}>
                    <Details.Summary>
                        <Details.Summary.Arrow/>
                        Erstellt ({successfulRows})
                    </Details.Summary>
                    <UsersImportResultTable content={result.created}/>
                </Details>
                <Details hidden={updatedRows === 0}>
                    <Details.Summary>
                        <Details.Summary.Arrow/>
                        Aktualisiert ({updatedRows})
                    </Details.Summary>
                    <UsersImportResultTable content={result.updated}/>
                </Details>
                <Details hidden={failedRows === 0}>
                    <Details.Summary>
                        <Details.Summary.Arrow/>
                        Übersprungen ({failedRows})
                    </Details.Summary>
                    <UsersImportErrorTable content={result.errors}/>
                </Details>
            </div>
        }

    </>;
};

export default UsersImportResult;
