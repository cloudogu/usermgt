import type {FormHandlerConfig} from "@cloudogu/ces-theme-tailwind";
import {Button, Form, H1, H3, Table, useAlertNotification, useFormHandler} from "@cloudogu/ces-theme-tailwind";
import React, {useEffect, useState} from "react";
import * as Yup from "yup";
import {t} from "../helpers/i18nHelpers";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import type {ImportUsersResponse} from "../services/ImportUsers";
import {ImportUsersService} from "../services/ImportUsers";

type ImportUsersUploadModel = {
    file?: File[];
    dryrun: boolean;
};

const UsersImport = (props: { title: string }) => {
    useSetPageTitle(props.title);
    const {notification, notify, clearNotification} = useAlertNotification();
    const [header, setHeader] = useState([""]);
    const [content, setContent] = useState([[""]]);
    const [uploadResult, setUploadResult] = useState<ImportUsersResponse>();
    const [fileSize, setFileSize] = useState(0);
    const [fileType, setFileType] = useState("");
    const cleanupPreview = () => {
        setHeader([]);
        setContent([]);
    };

    const handlerConfig: FormHandlerConfig<ImportUsersUploadModel> = {
        enableReinitialize: true,
        initialValues: {file: [], dryrun: false},
        validationSchema: Yup.object({}),
        onSubmit: async (values, formikHelpers) => {
            if (values.file?.length ?? 0 > 0) {
                const file = (values.file as File[])[0];
                const response = await ImportUsersService.save(file);
                cleanupPreview();
                setUploadResult(response.data);
                formikHelpers.resetForm();
            }
        }
    };
    const handler = useFormHandler(handlerConfig);

    useEffect(() => {
        const file = (handler.values["file"] ?? [])[0];
        if (file && file.type === "text/csv") {
            file.text().then(text => {
                const lines = text.split("\n");
                if (lines.length > 1) {
                    setHeader(lines[0].split(","));
                    const csvContent: string[][] = [];
                    for (let i = 1; i < lines.length; i++) {
                        if (lines[i].length > 0) {
                            csvContent.push(lines[i].split(","));
                        }
                    }
                    setContent(csvContent);
                    setFileSize(file.size);
                    setFileType(file.type);
                }
            });
        } else {
            setHeader([]);
            setContent([]);
            setFileSize(0);
            setFileType("");
        }
    }, [handler.values]);


    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.usersImport")}</H1>
        </div>
        <div>
            {notification}
            <Form handler={handler}>
                <Form.ValidatedCheckboxLabelRight id={"dryrun"} className={"ml-2"} name={"dryrun"}>
                    Dry run?
                </Form.ValidatedCheckboxLabelRight>
                <Form.HandledFileInput
                    className={"mt-4"}
                    variant={"primary"}
                    name={"file"}
                    accept={"text/csv"}
                />
                <Button variant={"primary"} type={"submit"}>Hochladen</Button>
            </Form>
            {uploadResult && renderResult(uploadResult)}

            {fileSize > 0 &&
                <>
                    <div>
                        <ul>
                            <li>{`size: ${fileSize / 1024} kB`}</li>
                            <li>{`type: ${fileType}`}</li>
                        </ul>
                    </div>


                    <Table className="my-4 text-sm" data-testid="users-table">
                        <Table.Head>
                            <Table.Head.Tr className={"uppercase"}>
                                {header.map((elem, i) => <Table.Head.Th key={`th-${i}-${elem}`}>{elem}</Table.Head.Th>)}
                            </Table.Head.Tr>
                        </Table.Head>
                        <Table.Body>
                            {
                                content.map(
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
        <H3>Import abgeschlossen</H3>
        <p>{`Erstellt: ${uploadResult.summary.CREATED}`}</p>
        <p>{`Aktualisiert: ${uploadResult.summary.UPDATED}`}</p>
        <p>{`Übersprungen: ${uploadResult.summary.SKIPPED}`}</p>
        <hr/>
        {uploadResult.errors && uploadResult.errors.length > 0 && <>
            <H3>Fehler</H3>
            <ul>
                {uploadResult.errors.map(err => <li key={err}>err</li>)}
            </ul>
        </>
        }
    </>;
}

export default UsersImport;
