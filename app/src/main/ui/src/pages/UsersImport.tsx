import {
    Button,
    Form,
    H1, H2,
    Href,
    LoadingIcon,
    useAlertNotification,
    useFormHandler
} from "@cloudogu/ces-theme-tailwind";
import React, {useState} from "react";
import {Navigate} from "react-router-dom";
import {twMerge} from "tailwind-merge";
import * as Yup from "yup";
import UsersImportTable from "../components/usersImport/UsersImportTable";
import {t} from "../helpers/i18nHelpers";
import useUserImportCsv from "../hooks/useUserImportCsv";
import {ImportUsersService} from "../services/ImportUsers";
import type {ImportUsersResponse} from "../services/ImportUsers";
import type {FormHandlerConfig} from "@cloudogu/ces-theme-tailwind";

type ImportUsersUploadModel = {
    file?: FileList;
    dryrun: boolean;
};
const UsersImport = () => {
    const {notification, notify} = useAlertNotification();
    const [loading, setLoading] = useState(false);
    const handlerConfig: FormHandlerConfig<ImportUsersUploadModel> = {
        enableReinitialize: true,
        initialValues: {file: undefined, dryrun: false},
        validationSchema: Yup.object({}),
        onSubmit: async (values, formikHelpers) => {
            if (values.file?.length ?? 0 > 0) {
                const file = values.file?.item(0) as File;
                setLoading(true);
                ImportUsersService.importCsv(file)
                    .then(response => {
                        setUploadResult(response.data);
                    })
                    .catch(e => {
                        notify(e.message, "danger");
                    })
                    .finally(() => {
                        formikHelpers.resetForm();
                        setLoading(false);
                    });
            }
        }
    };
    const handler = useFormHandler(handlerConfig);
    const {file} = useUserImportCsv(handler?.values?.file);
    const [uploadResult, setUploadResult] = useState<ImportUsersResponse | undefined>(undefined);

    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.usersImport")}</H1>
        </div>
        <div>
            {notification}
            <p className={"mt-4"}>{t("usersImport.info.text")}</p>
            <p className={"mt-1"}>
                {t("usersImport.info.further")}
                <Href href={t("usersImport.info.docsLink")}>{t("usersImport.info.docsLink")}</Href>
            </p>
            <Form handler={handler}>
                <Form.HandledFileInput
                    className={"mt-8"}
                    variant={"primary"}
                    name={"file"}
                    accept={"text/csv"}
                    onChange={() => {
                        setUploadResult(undefined);
                    }}
                />
                {(file !== undefined && !loading) &&
                    <>
                        <H2 className={"mt-12"}>{t("usersImport.headlines.table")}</H2>
                        <UsersImportTable header={file.header} rows={file.rows}/>
                    </>
                }
                <div className={"flex flex-row"}>
                    <Button
                        disabled={(file?.size ?? 0) === 0}
                        variant={"primary"}
                        type={"submit"}
                        className={twMerge("mt-4 flex-0 mr-4", (file?.size ?? 0) === 0 ? "" : "")}
                    >
                        {t("usersImport.buttons.upload")}
                    </Button>
                    <Button
                        disabled={(file?.size ?? 0) === 0}
                        variant={"secondary"}
                        type={"button"}
                        onClick={() => {
                            handler.resetForm();
                        }}
                        className={twMerge("mt-4 flex-0", (file?.size ?? 0) === 0 ? "" : "")}
                    >
                        {t("usersImport.buttons.reset")}
                    </Button>
                </div>
            </Form>
            {(uploadResult && !loading) &&
                <Navigate to={`/users/import/${uploadResult.importID}`} state={{result: uploadResult}}/>}
            {loading &&
                <div className={"w-full flex flex-row justify-center mt-16"}><LoadingIcon className={"w-64 h-64"}/>
                </div>}
        </div>
    </>;
};


export default UsersImport;
