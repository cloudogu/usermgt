import {
    Button,
    FileInput,
    Form,
    H1,
    H3,
    Table,
    useAlertNotification,
    useFormHandler
} from "@cloudogu/ces-theme-tailwind";
import React, {useEffect, useState} from "react";
import * as Yup from "yup";
import {t} from "../helpers/i18nHelpers";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import { ImportUsersService} from "../services/ImportUsers";
import type {ImportUsersResponse} from "../services/ImportUsers";
import type { FormHandlerConfig} from "@cloudogu/ces-theme-tailwind";

type ImportUsersUploadModel = {
    file?: File;
    dryrun: boolean;
};

const UsersImport = (props: { title: string }) => {
    useSetPageTitle(props.title);
    const {notification, notify, clearNotification} = useAlertNotification();
    const [file, setFile] = useState<File>();
    const [content, setContent] = useState([[""]]);
    const [header, setHeader] = useState([""]);
    const [uploadResult, setUploadResult] = useState<ImportUsersResponse>();
    // const uploadHandler = async () => {
    //     if (file) {
    //         const response = await ImportUsersService.save(file);
    //         cleanupPreview();
    //         setUploadResult(response.data);
    //     }
    // };
    const cleanupPreview = () => {
        setFile(undefined);
        setHeader([]);
        setContent([]);
    };
    const onFileChanged = async (event: React.ChangeEvent<HTMLInputElement>) => {
        const files = event.currentTarget.files;
        if (files !== null && files.length > 0) {
            const selectedFile = files[0];
            if (selectedFile.type !== "text/csv") {
                notify(`Wrong file type: '${selectedFile?.type}'. Only file type 'text/csv' is allowed.`, "danger");
                cleanupPreview();
                return;
            }
            await handler.setFieldValue("file", file);

            setFile(selectedFile);
            const fileContent = await selectedFile.text();
            const lines = fileContent.split("\n");
            setHeader(lines[0].split(","));
            const csvContent: string[][] = [];
            for (let i = 1; i < lines.length; i++) {
                if (lines[i].length > 0) {
                    csvContent.push(lines[i].split(","));
                }
            }
            setContent(csvContent);
        } else {
            cleanupPreview();
            return;
        }
        clearNotification();
    };

    useEffect(() => {
        const fileHelpers = handler.getFieldHelpers("file");
        fileHelpers.setValue(file);
    }, [file]);
    const handlerConfig: FormHandlerConfig<ImportUsersUploadModel> = {
        enableReinitialize: true,
        initialValues: {file: new File([new Blob()], ""), dryrun: false},
        validationSchema: Yup.object({}),
        onSubmit: async (values, formikHelpers) => {
            if (values.file) {
                const response = await ImportUsersService.save(values.file);
                cleanupPreview();
                setUploadResult(response.data);
                formikHelpers.resetForm();
            }
        }
    };
    const handler = useFormHandler(handlerConfig);

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
                <FileInput className={"mt-4"} variant={"primary"} name={"file"} accept={"text/csv"} onChange={onFileChanged} />
                <Button variant={"primary"} type={"submit"} >Hochladen</Button>
                {/*onClick={uploadHandler}*/}
            </Form>
            {uploadResult && renderResult(uploadResult)}

            {file && file?.type === "text/csv" &&
                <>
                    <div>
                        <ul>
                            <li>{`size: ${file?.size ?? 0 / 1024} kB`}</li>
                            <li>{`type: ${file?.type}`}</li>
                        </ul>
                    </div>


                    <Table className="my-4 text-sm" data-testid="users-table">
                        <Table.Head>
                            <Table.Head.Tr className={"uppercase"}>
                                {header.map((elem) => <Table.Head.Th key={elem}>{elem}</Table.Head.Th>)}
                            </Table.Head.Tr>
                        </Table.Head>
                        <Table.Body>
                            {content.map((entry) => <Table.Body.Tr key={entry.toString()}>
                                {entry.map((col) => <Table.Body.Td key={col}>{col}</Table.Body.Td>)}
                            </Table.Body.Tr>)}
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
