import {Table} from "@cloudogu/ces-theme-tailwind";
import React, {useState} from "react";

export default function UsersImportTable(file: { header: string[], rows: string[][] }) {
    const pageSize = 8;
    const [currentPage, setCurrentPage] = useState(1);
    const pageCount = Math.ceil(file.rows.length / pageSize);
    const startIndex = pageSize * (currentPage - 1);
    const endIndex = pageSize * currentPage;
    const entries = file.rows.filter((_, index) => index >= startIndex && index < endIndex);

    return (
        <Table className="my-4 text-sm min-w-[900px]" data-testid="users-table">
            <Table.Head>
                <Table.Head.Tr className={"uppercase"}>
                    {file.header.map((elem, i) => <Table.Head.Th
                        key={`th-${i}-${elem}`}>{elem}</Table.Head.Th>)}
                </Table.Head.Tr>
            </Table.Head>
            <Table.Body>
                {
                    entries.map(
                        (entry, i) =>
                            <Table.Body.Tr key={`row-${i}`}>
                                {entry.map((col, i) => <Table.Body.Td
                                    key={`col-${i}-${col}`}>{col}</Table.Body.Td>)}
                            </Table.Body.Tr>
                    )
                }
            </Table.Body>
            <Table.ConditionalFoot show={pageCount > 1}>
                <Table.Foot.Pagination
                    // className={"absolute bottom-0 left-1/2 -translate-x-1/2"}
                    pageCount={pageCount}
                    currentPage={currentPage}
                    onPageChange={(p) => {
                        setCurrentPage(p);
                    }}
                />
            </Table.ConditionalFoot>
        </Table>
    );
}