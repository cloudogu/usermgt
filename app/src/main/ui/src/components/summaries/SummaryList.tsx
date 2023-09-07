import {CesIcons, DropdownMenu, Table, TextWithIcon} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {t} from "../../helpers/i18nHelpers";
import type {ImportSummary} from "../../services/ImportUsers";

export interface SummaryListProps {
    summaries: ImportSummary[],
    pageCount: number;
    currentPage: number;
    onPageChange: (_newPage: number) => void;
    isLoading: boolean;
}

export default function SummaryList({summaries, pageCount, currentPage, onPageChange, isLoading}: SummaryListProps) {
    return (
        <Table>
            <Table.Head>
                <Table.Head.Tr>
                    <Table.Head.Th>
                        {t("summaries.table.headline.name")}
                    </Table.Head.Th>
                    <Table.Head.Th>
                        {t("summaries.table.headline.date")}
                    </Table.Head.Th>
                    <Table.Head.Th>
                        {t("summaries.table.headline.result")}
                    </Table.Head.Th>
                    <Table.Head.Th>
                        {t("summaries.table.headline.functions")}
                    </Table.Head.Th>
                </Table.Head.Tr>
            </Table.Head>
            <Table.ConditionalBody show={!isLoading}>
                {
                    summaries.map((s, i) => (
                        <Table.Body.Tr key={i}>
                            <Table.Body.Td>
                                {s.filename}
                            </Table.Body.Td>
                            <Table.Body.Td>
                                {s.timestamp.toUTCString()}
                            </Table.Body.Td>
                            <Table.Body.Td>
                                {t("summaries.result.created")}: {s.summary.created}{", "}
                                {t("summaries.result.updated")}: {s.summary.updated}{", "}
                                {t("summaries.result.errors")}: {s.summary.skipped}
                            </Table.Body.Td>
                            <Table.Body.Td className={"flex flex-row"}>
                                <DropdownMenu>
                                    <DropdownMenu.Button>
                                        {t("summaries.table.functions")}
                                        <DropdownMenu.Button.Arrow/>
                                    </DropdownMenu.Button>
                                    <DropdownMenu.Items>
                                        <DropdownMenu.Items.LinkItem
                                            href={`/usermgt/api/users/import/${s.importID}/download`}
                                            className={"flex flex-row"}
                                        >
                                            <TextWithIcon icon={<CesIcons.DownloadSimple weight={"bold"}/>}>
                                                {t("summaries.table.function.download")}
                                            </TextWithIcon>
                                        </DropdownMenu.Items.LinkItem>
                                        <DropdownMenu.Items.RouterLinkItem
                                            to={`/users/import/${s.importID}`}
                                            className={"flex"}
                                        >
                                            <TextWithIcon icon={<CesIcons.Table weight={"bold"}/>}>
                                                {t("summaries.table.function.details")}
                                            </TextWithIcon>
                                        </DropdownMenu.Items.RouterLinkItem>
                                        <DropdownMenu.Items.ButtonItem className={"flex flex-row"}>
                                            <TextWithIcon icon={<CesIcons.TrashSimple weight={"bold"}/>}>
                                                {t("summaries.table.function.delete")}
                                            </TextWithIcon>
                                        </DropdownMenu.Items.ButtonItem>
                                    </DropdownMenu.Items>
                                </DropdownMenu>
                            </Table.Body.Td>
                        </Table.Body.Tr>
                    ))
                }
            </Table.ConditionalBody>
            <Table.ConditionalFoot show={pageCount > 1}>
                <Table.Foot.Pagination pageCount={pageCount} currentPage={currentPage} onPageChange={onPageChange}/>
            </Table.ConditionalFoot>
        </Table>
    );
}