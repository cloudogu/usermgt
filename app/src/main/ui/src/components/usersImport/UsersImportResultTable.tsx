import {Table} from "@cloudogu/ces-theme-tailwind";
import React from "react";

import type {ImportedUser} from "../../services/ImportUsers";
import type {ComponentPropsWithoutRef} from "react";
import {User} from "../../services/Users";

export interface UsersImportResultTableProps extends Omit<ComponentPropsWithoutRef<"table">, "content"> {
    content: ImportedUser[];
}

export default function UsersImportResultTable({content, ...props}: UsersImportResultTableProps) {
    if ((content ?? []).length === 0) {
        return (<></>);
    }
    const columnWidths = [
        "w-[12.5%]",
        "w-[12.5%]",
        "w-[12.5%]",
        "w-[12.5%]",
        "w-[12.5%]",
        "w-[12.5%]",
        "w-[12.5%]",
    ];

    return (
        <Table {...props} className={"table-fixed min-w-[900px]"}>
            <Table.Head>
                <Table.Head.Tr>
                    {
                        [
                            "Nutzername",
                            "Vorname",
                            "Nachname",
                            "Anzeigename",
                            "Email",
                            "Extern",
                            "Passwort temporär"
                        ].map((k, i) =>
                            <Table.Head.Th key={k} className={`${columnWidths[i]} break-all`}>
                                {k}
                            </Table.Head.Th>
                        )
                    }
                </Table.Head.Tr>
            </Table.Head>
            <Table.Body>
                {
                    content.map((c, key) => {
                        const user = (c as any) || {};
                        return (
                            <Table.Body.Tr key={key}>
                                {[
                                    "username",
                                    "givenname",
                                    "surname",
                                    "displayName",
                                    "mail",
                                    "external",
                                    "passwordReset",
                                ]
                                    .map(
                                        (h,i) => {
                                            const isBoolean = h === "external" || h === "passwordReset";
                                            const isString = !isBoolean;
                                            return (
                                                <Table.Body.Td key={h} className={`${columnWidths[i]} break-all`}>
                                                    {isBoolean && ((user[h] as boolean) ? "Ja" : "Nein")}
                                                    {isString && user[h]}
                                                </Table.Body.Td>
                                            );
                                        }
                                    )
                                }
                            </Table.Body.Tr>
                        );
                    })
                }
            </Table.Body>
        </Table>
    );
}