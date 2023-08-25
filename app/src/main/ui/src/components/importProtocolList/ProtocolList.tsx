import {CesIcons, Href, Table} from "@cloudogu/ces-theme-tailwind";
import type {ImportProtocol} from "../../services/ImportUsers";
import React from "react";
import {Link} from "react-router-dom";

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
                        Name
                    </Table.Head.Th>
                    <Table.Head.Th>
                        Datum
                    </Table.Head.Th>
                    <Table.Head.Th>
                        Result
                    </Table.Head.Th>
                    <Table.Head.Th>
                        Actions
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
                                Neu: {p.result.created.length},
                                Aktualisiert: {p.result.updated.length},
                                Übersprungen: {p.result.errors.length}
                            </Table.Body.Td>
                            <Table.Body.Td className={"flex flex-row"}>
                                <a href={`/usermgt/protocol/download/${p.result.timestamp.getTime()}`}>
                                    <CesIcons.DownloadSimple weight={"bold"} className={"w-6 h-6"}/>
                                </a>
                                <Link to={"/users/import/results"} state={p.result}>
                                    <CesIcons.Link weight={"bold"} className={"w-6 h-6"} />
                                </Link>
                                <button>
                                    <CesIcons.TrashSimple weight={"bold"} className={"w-6 h-6"} />
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