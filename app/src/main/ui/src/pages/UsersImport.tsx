import {Button, Form, H1, H3, Table, useAlertNotification, useFormHandler} from "@cloudogu/ces-theme-tailwind";
import React, {useState} from "react";
import {twMerge} from "tailwind-merge";
import * as Yup from "yup";
import {t} from "../helpers/i18nHelpers";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import useUserImportCsv from "../hooks/useUserImportCsv";
import {ImportUsersService} from "../services/ImportUsers";
import type {FormHandlerConfig} from "@cloudogu/ces-theme-tailwind";


type ImportUsersUploadModel = {
    file?: FileList;
    dryrun: boolean;
};

export interface ImportUsersResponse {
    summary: {
        CREATED: number;
        UPDATED: number;
        SKIPPED: number;
    },
    errors: string[];
}

const UsersImport = (props: { title: string }) => {
    useSetPageTitle(props.title);
    const {notification, notify} = useAlertNotification();
    const handlerConfig: FormHandlerConfig<ImportUsersUploadModel> = {
        enableReinitialize: true,
        initialValues: {file: undefined, dryrun: false},
        validationSchema: Yup.object({}),
        onSubmit: async (values, formikHelpers) => {
            if (values.file?.length ?? 0 > 0) {
                const file = values.file?.item(0) as File;
                ImportUsersService.save(file)
                    .then(response => {
                        setUploadResult(response.data);
                    })
                    .catch(e => {
                        notify(e.message, "danger");
                    })
                    .finally(() => {
                        formikHelpers.resetForm();
                    });
            }
        }
    };
    const handler = useFormHandler(handlerConfig);
    const {file} = useUserImportCsv(handler?.values?.file);
    const [uploadResult, setUploadResult] = useState<ImportUsersResponse>();

    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.usersImport")}</H1>
        </div>
        <div>
            {notification}
            <Form handler={handler}>
                <Form.HandledFileInput
                    className={"mt-4"}
                    variant={"primary"}
                    name={"file"}
                    accept={"text/csv"}
                />
                <Button variant={"primary"} type={"submit"} className={"mt-4"}>{t("usersImport.buttons.upload")}</Button>
            </Form>
            {uploadResult && renderResult(uploadResult)}

            {file !== undefined &&
                <>
                    <H3 className={"mt-12"}>{t("usersImport.headlines.table")}</H3>
                    <Table className="my-4 text-sm" data-testid="users-table">
                        <Table.Head>
                            <Table.Head.Tr className={"uppercase"}>
                                {file.header.map((elem, i) => <Table.Head.Th
                                    key={`th-${i}-${elem}`}>{elem}</Table.Head.Th>)}
                            </Table.Head.Tr>
                        </Table.Head>
                        <Table.Body>
                            {
                                file.rows.map(
                                    (entry, i) =>
                                        <Table.Body.Tr key={`row-${i}`}>
                                            {entry.map((col, i) => <Table.Body.Td
                                                key={`col-${i}-${col}`}>{col}</Table.Body.Td>)}
                                        </Table.Body.Tr>
                                )
                            }
                        </Table.Body>
                    </Table>
                </>
            }

        </div>
    </>;
};

function renderResult(uploadResult: ImportUsersResponse) {
    return <>
        <H3>{t("usersImport.headlines.importSuccess")}</H3>
        <p>{`Erstellt: ${uploadResult.summary.CREATED}`}</p>
        <p>{`Aktualisiert: ${uploadResult.summary.UPDATED}`}</p>
        <p>{`Übersprungen: ${uploadResult.summary.SKIPPED}`}</p>
        {uploadResult.errors && uploadResult.errors.length > 0 && <>
            <H3>{t("usersImport.headlines.importErrors")}</H3>
            <ul>
                {uploadResult.errors.map(err => <li key={err}>err</li>)}
            </ul>
        </>
        }
    </>;
}

export default UsersImport;
