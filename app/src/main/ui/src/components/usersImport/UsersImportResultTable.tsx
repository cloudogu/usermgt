import {Table} from "@cloudogu/ces-theme-tailwind";
import type {ComponentPropsWithoutRef} from "react";
import React from "react";

import type {ImportedUser} from "../../services/ImportUsers";

export interface UsersImportResultTableProps extends Omit<ComponentPropsWithoutRef<"table">, "content"> {
    content: ImportedUser[];
}

export default function UsersImportResultTable({content, ...props}: UsersImportResultTableProps) {
    if ((content ?? []).length === 0) {
        return (<></>);
    }

    const createdHeadlines = Object.keys(content[0] ?? {}) ?? [];
    console.log(createdHeadlines);

    return (
        <Table {...props} className={"table-fixed"}>
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
                            "Password-Reset",
                            "Gruppen"
                        ].map(k =>
                            <Table.Head.Th key={k} className={"w-[12.5%] break-all"}>
                                {k}
                            </Table.Head.Th>
                        )
                    }
                </Table.Head.Tr>
            </Table.Head>
            <Table.Body>
                {
                    content.map((c, i) =>
                        <Table.Body.Tr key={i}>
                            {[
                                "username",
                                "givenname",
                                "surname",
                                "displayName",
                                "mail",
                                "external",
                                "passwordReset",
                                "memberOf",
                            ]
                                .map(
                                    h => {
                                        const isBoolean = h === "external" || h === "passwordReset";
                                        const isArray = h === "memberOf";
                                        const isString = !isBoolean && !isArray;

                                        return (
                                            <Table.Body.Td key={h} className={"w-[12.5%] break-all"}>
                                                {isBoolean && ((content[i]) ? "TRUE" : "FALSE")}
                                                {isString && ((content[i] as any)[h])}
                                                {isArray && ((content[i] as any)[h] as string[]).join(", ")}
                                            </Table.Body.Td>
                                        );
                                    }
                                )
                            }
                        </Table.Body.Tr>
                    )
                }
            </Table.Body>
        </Table>
    );
}