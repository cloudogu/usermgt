import {Table} from "@cloudogu/ces-theme-tailwind";
import React from "react";


import {t} from "../../helpers/i18nHelpers";
import type {ImportedUser} from "../../services/ImportUsers";
import type {ComponentPropsWithoutRef} from "react";

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
        "w-[20%]",
        "w-[7.5%]",
        "w-[7.5%]",
    ];

    return (
        <Table {...props} className={"table-fixed min-w-[900px]"}>
            <Table.Head>
                <Table.Head.Row>
                    {
                        [
                            t("usersImportResult.table.success.username"),
                            t("usersImportResult.table.success.givenName"),
                            t("usersImportResult.table.success.name"),
                            t("usersImportResult.table.success.displayName"),
                            t("usersImportResult.table.success.mail"),
                            t("usersImportResult.table.success.external"),
                            t("usersImportResult.table.success.passwordReset"),
                        ].map((k, i) =>
                            <Table.Head.Row.Column key={k} className={`${columnWidths[i]} break-word`}>
                                {k}
                            </Table.Head.Row.Column>
                        )
                    }
                </Table.Head.Row>
            </Table.Head>
            <Table.Body>
                {
                    content.map((c, key) => {
                        const user = (c as any) || {};
                        return (
                            <Table.Body.Row key={key}>
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
                                                <Table.Body.Row.Column key={h} className={`${columnWidths[i]} break-all`}>
                                                    {isBoolean && ((user[h] as boolean) ? t("usersImportResult.table.success.true") : t("usersImportResult.table.success.false"))}
                                                    {isString && user[h]}
                                                </Table.Body.Row.Column>
                                            );
                                        }
                                    )
                                }
                            </Table.Body.Row>
                        );
                    })
                }
            </Table.Body>
        </Table>
    );
}
