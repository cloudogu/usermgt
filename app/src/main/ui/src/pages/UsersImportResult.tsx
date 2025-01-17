import {Details, H1, Href, LoadingIcon, Paragraph} from "@cloudogu/deprecated-ces-theme-tailwind";
import React, {useState} from "react";
import {useLocation, useNavigate, useParams} from "react-router-dom";
import UsersImportErrorTable from "../components/usersImport/UsersImportErrorTable";
import UsersImportResultTable from "../components/usersImport/UsersImportResultTable";
import {t} from "../helpers/i18nHelpers";
import {useAPI} from "../hooks/useAPI";
import {ImportUsersService} from "../services/ImportUsers";
import type {ImportUsersResponse} from "../services/ImportUsers";
import type {Location} from "history";

const UsersImportResult = () => {
    const navigate = useNavigate();
    const {state} = useLocation() as Location<{
        result?: ImportUsersResponse,
    }>;

    const r = state?.result;
    const {id} = useParams();
    const {isLoading} = useAPI<ImportUsersResponse | undefined>(
        async (signal) => {
            if (!r) {
                ImportUsersService.getImportDetails(id ?? "", signal)
                    .then((details) => {
                        navigate(".", {state: {result: details.data}, replace: true});
                        setError(false);
                        return details.data;
                    })
                    .catch(() => {
                        setError(true);
                    });
            }
            return r;
        }
    );

    const [error, setError] = useState(false);
    const summary = state?.result;

    const createdRows = summary?.created?.length ?? 0;
    const updatedRows = summary?.updated?.length ?? 0;
    const failedRows = summary?.errors?.length ?? 0;
    const successfulRows = createdRows + updatedRows;
    const affectedRows = successfulRows + failedRows;

    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.usersImportResult")}</H1>
        </div>
        {((!summary && !id) || error) &&
            <Paragraph className={"mt-6"}>
                {t("usersImportResult.error")}
            </Paragraph>
        }
        {(!summary && isLoading) &&
            <>
                <LoadingIcon className={"w-64 h-64"}/>
            </>
        }
        {(summary && affectedRows > 0 && !isLoading) &&
            <>
                <Paragraph className={"mb-8 mt-2"} data-testid={"import-status-message"}>
                    {failedRows === 0 && t("usersImportResult.result.success")}
                    {(failedRows > 0 && successfulRows > 0) && t("usersImportResult.result.successWithFailures")}
                    {successfulRows === 0 && t("usersImportResult.result.failure")}
                </Paragraph>
                <Details hidden={createdRows === 0} data-testid={"created-import-details"}>
                    <Details.Summary>
                        <Details.Summary.Arrow/>
                        {t("usersImportResult.rows.created")} ({createdRows})
                    </Details.Summary>
                    <UsersImportResultTable content={summary.created}/>
                </Details>
                <Details hidden={updatedRows === 0} data-testid={"updated-import-details"}>
                    <Details.Summary>
                        <Details.Summary.Arrow/>
                        {t("usersImportResult.rows.updated")} ({updatedRows})
                    </Details.Summary>
                    <UsersImportResultTable content={summary.updated}/>
                </Details>
                <Details hidden={failedRows === 0} data-testid={"skipped-import-details"}>
                    <Details.Summary>
                        <Details.Summary.Arrow/>
                        {t("usersImportResult.rows.skipped")} ({failedRows})
                    </Details.Summary>
                    <UsersImportErrorTable content={summary.errors}/>
                </Details>

                <Paragraph className={"mt-6"} data-testid={"import-download-link"}>
                    <Href href={ImportUsersService.createDownloadLink(summary)}>{t("usersImportResult.download")}</Href>
                </Paragraph>
            </>
        }
    </>;
};

export default UsersImportResult;
