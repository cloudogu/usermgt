import {Table} from "@cloudogu/ces-theme-tailwind";
import type {ComponentPropsWithoutRef} from "react";
import React from "react";
import type {ImportError} from "../../services/ImportUsers";
import {t} from "../../helpers/i18nHelpers";

export interface UsersImportErrorTableProps extends Omit<ComponentPropsWithoutRef<"table">, "content"> {
    content: ImportError[];
}

export default function UsersImportErrorTable({content, ...props}: UsersImportErrorTableProps) {
    if ((content ?? []).length === 0) {
        return (<></>);
    }

    // const createdHeadlines = Object.keys(content[0] ?? {}) ?? [];

    return (
        <Table {...props}>
            <Table.Head>
                <Table.Head.Tr>
                    <Table.Head.Th>
                        Zeile
                    </Table.Head.Th>
                    <Table.Head.Th>
                        Fehler
                    </Table.Head.Th>
                </Table.Head.Tr>
            </Table.Head>
            <Table.Body>
                {
                    content.map((c, i) =>
                        <Table.Body.Tr key={i}>
                            <Table.Body.Td>
                                {content[i].lineNumber}
                            </Table.Body.Td>
                            <Table.Body.Td>
                                {prepareMessage(content[i])}
                            </Table.Body.Td>
                        </Table.Body.Tr>
                    )
                }
            </Table.Body>
        </Table>
    );
}

function prepareMessage(error: ImportError): string {
    return t(`usersImportResult.msg.code-${error.code}`, {columns: error.params.columns.join(", ")});
}