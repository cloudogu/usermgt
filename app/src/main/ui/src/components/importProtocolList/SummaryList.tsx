import {CesIcons, DropdownMenu, Table, TextWithIcon} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {t} from "../../helpers/i18nHelpers";
import type {ImportSummary} from "../../services/ImportUsers";

export interface ProtocolListProps {
    protocols: ImportSummary[],
    pageCount: number;
    currentPage: number;
    onPageChange: (_newPage: number) => void;
}

export default function SummaryList({protocols, pageCount, currentPage, onPageChange}: ProtocolListProps) {
    return (
        <Table>
            <Table.Head>
                <Table.Head.Tr>
                    <Table.Head.Th>
                        {t("importProtocols.table.headline.name")}
                    </Table.Head.Th>
                    <Table.Head.Th>
                        {t("importProtocols.table.headline.date")}
                    </Table.Head.Th>
                    <Table.Head.Th>
                        {t("importProtocols.table.headline.result")}
                    </Table.Head.Th>
                    <Table.Head.Th>
                        {t("importProtocols.table.headline.functions")}
                    </Table.Head.Th>
                </Table.Head.Tr>
            </Table.Head>
            <Table.Body>
                {
                    protocols.map((s, i) => (
                        <Table.Body.Tr key={i}>
                            <Table.Body.Td>
                                {s.filename}
                            </Table.Body.Td>
                            <Table.Body.Td>
                                {s.timestamp.toUTCString()}
                            </Table.Body.Td>
                            <Table.Body.Td>
                                {t("importProtocols.result.created")}: {s.summary.created}{", "}
                                {t("importProtocols.result.updated")}: {s.summary.updated}{", "}
                                {t("importProtocols.result.errors")}: {s.summary.skipped}
                            </Table.Body.Td>
                            <Table.Body.Td className={"flex flex-row"}>
                                <DropdownMenu>
                                    <DropdownMenu.Button>
                                        {t("importProtocols.table.functions")}
                                        <DropdownMenu.Button.Arrow/>
                                    </DropdownMenu.Button>
                                    <DropdownMenu.Items>
                                        <DropdownMenu.Items.LinkItem
                                            href={`/usermgt/protocol/download/${s.timestamp.getTime()}`}
                                            className={"flex flex-row"}
                                        >
                                            <TextWithIcon icon={<CesIcons.DownloadSimple weight={"bold"}/>}>
                                                {t("importProtocols.table.function.download")}
                                            </TextWithIcon>
                                        </DropdownMenu.Items.LinkItem>
                                        <DropdownMenu.Items.RouterLinkItem
                                            to={`/usermgt/api/users/import/${s.importID}/download`}
                                            className={"flex"}
                                        >
                                            <TextWithIcon icon={<CesIcons.Table weight={"bold"}/>}>
                                                {t("importProtocols.table.function.details")}
                                            </TextWithIcon>
                                        </DropdownMenu.Items.RouterLinkItem>
                                        <DropdownMenu.Items.ButtonItem className={"flex flex-row"}>
                                            <TextWithIcon icon={<CesIcons.TrashSimple weight={"bold"}/>}>
                                                {t("importProtocols.table.function.delete")}
                                            </TextWithIcon>
                                        </DropdownMenu.Items.ButtonItem>
                                    </DropdownMenu.Items>
                                </DropdownMenu>
                            </Table.Body.Td>
                        </Table.Body.Tr>
                    ))
                }
            </Table.Body>
            <Table.ConditionalFoot show={pageCount > 1}>
                <Table.Foot.Pagination pageCount={pageCount} currentPage={currentPage} onPageChange={onPageChange}/>
            </Table.ConditionalFoot>
        </Table>
    );
}