import {Details, H1, Href, Paragraph} from "@cloudogu/ces-theme-tailwind";
import React, {useEffect, useState} from "react";
import {useLocation, useParams} from "react-router-dom";
import UsersImportErrorTable from "../components/usersImport/UsersImportErrorTable";
import UsersImportResultTable from "../components/usersImport/UsersImportResultTable";
import {t} from "../helpers/i18nHelpers";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import {ImportUsersService} from "../services/ImportUsers";
import type {ImportUsersResponse} from "../services/ImportUsers";
import type {Location} from "history";

const UsersImportResult = (props: { title: string }) => {
    const {state} = useLocation() as Location<{
        result?: ImportUsersResponse,
    }>;
    const r = state?.result;
    const {id} = useParams();
    const [result, setResult] = useState(r);
    const [error, setError] = useState(false);
    useSetPageTitle(props.title);

    const createdRows = result?.created?.length ?? 0;
    const updatedRows = result?.updated?.length ?? 0;
    const failedRows = result?.errors?.length ?? 0;
    const successfulRows = createdRows + updatedRows;
    const affectedRows = successfulRows + failedRows;

    useEffect(() => {
        if (!r && id) {
            ImportUsersService.getImportDetails(id ?? "")
                .then((r) => {
                    setResult(r.data);
                })
                .catch(() => {
                    setError(true);
                });
        }
    }, [id]);

    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.usersImportResult")}</H1>
        </div>
        {((!result && !id) || error) &&
            <Paragraph className={"mt-6"}>
                {t("usersImportResult.error")}
            </Paragraph>
        }
        {!result &&
            <></>
        }
        {result && affectedRows > 0 &&
            <>
                <Paragraph className={"mb-8 mt-2"}>
                    {failedRows === 0 && t("usersImportResult.result.success")}
                    {failedRows > 0 && t("usersImportResult.result.successWithFailures")}
                    {successfulRows === 0 && t("usersImportResult.result.failure")}
                </Paragraph>
                <Details hidden={createdRows === 0}>
                    <Details.Summary>
                        <Details.Summary.Arrow/>
                        Erstellte Nutzerkonten ({successfulRows})
                    </Details.Summary>
                    <UsersImportResultTable content={result.created}/>
                </Details>
                <Details hidden={updatedRows === 0}>
                    <Details.Summary>
                        <Details.Summary.Arrow/>
                        Aktualisierte Nutzerkonten ({updatedRows})
                    </Details.Summary>
                    <UsersImportResultTable content={result.updated}/>
                </Details>
                <Details hidden={failedRows === 0}>
                    <Details.Summary>
                        <Details.Summary.Arrow/>
                        Übersprungene Datenzeilen ({failedRows})
                    </Details.Summary>
                    <UsersImportErrorTable content={result.errors}/>
                </Details>

                <Paragraph className={"mt-6"}>
                    <Href href={`/usermgt/api/users/import/${result.importID}/download`}>Protokoll
                        herunterladen</Href>
                </Paragraph>
            </>
        }
    </>;
};

export default UsersImportResult;
