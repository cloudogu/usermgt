import {Table} from "@cloudogu/ces-theme-tailwind";
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