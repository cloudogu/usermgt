import {Button, CesIcons, DropdownMenu, Href, RouterLink, Table, TextWithIcon} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {Link} from "react-router-dom";
import {t} from "../../helpers/i18nHelpers";
import {ImportUsersService} from "../../services/ImportUsers";
import type {ImportProtocol} from "../../services/ImportUsers";

export interface ProtocolListProps {
    protocols: ImportProtocol[],
    pageCount: number;
    currentPage: number;
    onPageChange: (_newPage: number) => void;
}

export default function ProtocolList({protocols, pageCount, currentPage, onPageChange}: ProtocolListProps) {
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
                    protocols.map((p, i) => (
                        <Table.Body.Tr key={i}>
                            <Table.Body.Td>
                                {p.name}
                            </Table.Body.Td>
                            <Table.Body.Td>
                                {p.timestamp.toUTCString()}
                            </Table.Body.Td>
                            <Table.Body.Td>
                                {t("importProtocols.result.created")}: {p.result.created}{", "}
                                {t("importProtocols.result.updated")}: {p.result.updated}{", "}
                                {t("importProtocols.result.errors")}: {p.result.skipped}
                            </Table.Body.Td>
                            <Table.Body.Td className={"flex flex-row"}>
                                <DropdownMenu>
                                    <DropdownMenu.Button>
                                        Funktionen
                                        <DropdownMenu.Button.Arrow/>
                                    </DropdownMenu.Button>
                                    <DropdownMenu.Items>
                                        <DropdownMenu.Items.LinkItem
                                            href={`/usermgt/protocol/download/${p.timestamp.getTime()}`}
                                            className={"flex flex-row"}
                                        >
                                            <TextWithIcon icon={<CesIcons.DownloadSimple weight={"bold"}/>}>
                                                Herunterladen
                                            </TextWithIcon>
                                        </DropdownMenu.Items.LinkItem>
                                        <DropdownMenu.Items.RouterLinkItem
                                            to={"/users/import/results"}
                                            state={{protocol: p.result}}
                                            className={"flex"}
                                        >
                                            <TextWithIcon icon={<CesIcons.Table weight={"bold"}/>}>
                                                Details
                                            </TextWithIcon>
                                        </DropdownMenu.Items.RouterLinkItem>
                                        <DropdownMenu.Items.ButtonItem className={"flex flex-row"}>
                                            <TextWithIcon icon={<CesIcons.TrashSimple weight={"bold"}/>}>
                                                Löschen
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