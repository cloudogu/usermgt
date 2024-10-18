import {ActionTableFrontendPaginated, ActionTableRoot, usePaginationControl} from "@cloudogu/ces-theme-tailwind";
import React from "react";

export default function UsersImportTable(file: { header: string[], rows: string[][] }) {
    const paginationControl = usePaginationControl(
        {
            lineCountOptions: [25, 50, 100],
            allLineCount: file.rows.length,
            defaultStartPage: 1,
            defaultLinesPerPage: 25
        }
    );

    return (
        <ActionTableRoot paginationControl={paginationControl}>
            <ActionTableFrontendPaginated<string[]> values={file.rows}>
                {(values: string[][]) => <>
                    <ActionTableFrontendPaginated.HeadWithOneRow>
                        {
                            file
                                .header
                                .map(column => <ActionTableFrontendPaginated.HeadWithOneRow.Column key={column} className={"uppercase"}>
                                    {column}
                                </ActionTableFrontendPaginated.HeadWithOneRow.Column>
                                )
                        }
                    </ActionTableFrontendPaginated.HeadWithOneRow>
                    <ActionTableFrontendPaginated.Body>
                        {values.map((row: string[], rowIndex) => (
                            <ActionTableFrontendPaginated.Body.Row key={rowIndex}>
                                {row.map((column, columnIndex) => (
                                    <ActionTableFrontendPaginated.Body.Row.Column key={`${column}-${rowIndex}-${columnIndex}`}>
                                        {column}
                                    </ActionTableFrontendPaginated.Body.Row.Column>
                                ))}
                            </ActionTableFrontendPaginated.Body.Row>
                        ))}
                    </ActionTableFrontendPaginated.Body>
                </>}
            </ActionTableFrontendPaginated>
        </ActionTableRoot>
    );
}
