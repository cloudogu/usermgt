import {Table} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {t} from "../../helpers/i18nHelpers";
import type {ImportError} from "../../services/ImportUsers";
import type {ComponentPropsWithoutRef} from "react";

export interface UsersImportErrorTableProps extends Omit<ComponentPropsWithoutRef<"table">, "content"> {
    content: ImportError[];
}

export default function UsersImportErrorTable({content, ...props}: UsersImportErrorTableProps) {
    if ((content ?? []).length === 0) {
        return (<></>);
    }

    return (
        <Table {...props} className={"min-w-[900px]"}>
            <Table.Head>
                <Table.Head.Row>
                    <Table.Head.Row.Column>
                        {t("usersImportResult.table.errors.line")}
                    </Table.Head.Row.Column>
                    <Table.Head.Row.Column>
                        {t("usersImportResult.table.errors.error")}
                    </Table.Head.Row.Column>
                </Table.Head.Row>
            </Table.Head>
            <Table.Body>
                {
                    content.map((c, i) =>
                        <Table.Body.Row key={i}>
                            <Table.Body.Row.Column>
                                {content[i].lineNumber}
                            </Table.Body.Row.Column>
                            <Table.Body.Row.Column>
                                {prepareMessage(content[i])}
                            </Table.Body.Row.Column>
                        </Table.Body.Row>
                    )
                }
            </Table.Body>
        </Table>
    );
}

function prepareMessage(error: ImportError): string {
    return t(`usersImportResult.msg.code-${error.errorCode}`, {
        columns: error.params?.columns?.join(", ") ?? "",
        values: error.params?.values?.join(", ") ?? "",
    });
}
