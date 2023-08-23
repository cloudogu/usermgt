import {Details, H1} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {useLocation} from "react-router-dom";
import {t} from "../helpers/i18nHelpers";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import type {ImportUsersResponse} from "../services/ImportUsers";
import type {Location} from "history";

const UsersImportResult = (props: { title: string }) => {
    const {state: result} = useLocation() as Location<ImportUsersResponse>;
    useSetPageTitle(props.title);

    console.log(result);

    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.usersImportResult")}</H1>
        </div>
        { !result &&
            <div>
                ERROR
            </div>
        }
        { result &&
            <div>
                <Details>
                    <Details.Summary>Erstellt</Details.Summary>
                    TABELLE
                </Details>
                <Details>
                    <Details.Summary>Aktualisiert</Details.Summary>
                    TABELLE
                </Details>
                <Details>
                    <Details.Summary>Übersprungen</Details.Summary>
                    TABELLE
                </Details>
            </div>
        }

    </>;
};

export default UsersImportResult;
