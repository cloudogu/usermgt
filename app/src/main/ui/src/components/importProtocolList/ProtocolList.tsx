import {CesIcons, Table} from "@cloudogu/ces-theme-tailwind";
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
                                {p.result.timestamp.toUTCString()}
                            </Table.Body.Td>
                            <Table.Body.Td>
                                {t("importProtocols.result.created")}: {p.result.created.length}{", "}
                                {t("importProtocols.result.updated")}: {p.result.updated.length}{", "}
                                {t("importProtocols.result.errors")}: {p.result.errors.length}
                            </Table.Body.Td>
                            <Table.Body.Td className={"flex flex-row"}>
                                <a
                                    href={`/usermgt/protocol/download/${p.result.timestamp.getTime()}`}
                                    aria-description={t("importProtocols.aria.download", {identifier: p.name})}
                                >
                                    <CesIcons.DownloadSimple weight={"bold"} className={"w-6 h-6"} aria-hidden={true}/>
                                </a>
                                <Link to={"/users/import/results"} 
                                    state={p.result}
                                    aria-description={t("importProtocols.aria.info", {identifier: p.name})}
                                >
                                    <CesIcons.Link weight={"bold"} className={"w-6 h-6"} aria-hidden={true}/>
                                </Link>
                                <button
                                    onClick={() => ImportUsersService.deleteProtocol(p)}
                                    aria-description={t("importProtocols.aria.delete", {identifier: p.name})}
                                >
                                    <CesIcons.TrashSimple weight={"bold"} className={"w-6 h-6"} aria-hidden={true}/>
                                </button>
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