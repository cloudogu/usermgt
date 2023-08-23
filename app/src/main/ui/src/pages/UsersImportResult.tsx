import {Details, H1, Table} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {useLocation} from "react-router-dom";
import {t} from "../helpers/i18nHelpers";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import type {ImportUsersResponse} from "../services/ImportUsers";
import type {Location} from "history";

const UsersImportResult = (props: { title: string }) => {
    const {state: result} = useLocation() as Location<ImportUsersResponse>;
    useSetPageTitle(props.title);

    const createdHeadlines = Object.keys(result.created[0] ?? {}) ?? [];


    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.usersImportResult")}</H1>
        </div>
        {!result &&
            <div>
                ERROR
            </div>
        }
        {result &&
            <div>
                <Details>
                    <Details.Summary>Erstellt</Details.Summary>
                    <Table>
                        <Table.Head>
                            <Table.Head.Tr>
                                {
                                    createdHeadlines.map(k =>
                                        <Table.Head.Th key={k}>
                                            {k}
                                        </Table.Head.Th>
                                    )
                                }
                            </Table.Head.Tr>
                        </Table.Head>
                        <Table.Body>
                            {
                                result?.created?.map((c, i) =>
                                    <Table.Body.Tr key={i}>
                                        {createdHeadlines.map(h =>
                                            <Table.Body.Td key={h}>
                                                {(result?.created[i] as any)[h]}
                                            </Table.Body.Td>
                                        )}
                                    </Table.Body.Tr>
                                )
                            }
                        </Table.Body>
                    </Table>
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
