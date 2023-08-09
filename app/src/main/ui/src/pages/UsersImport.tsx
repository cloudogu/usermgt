import {FileInput, H1} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {t} from "../helpers/i18nHelpers";
import {useSetPageTitle} from "../hooks/useSetPageTitle";


const UsersImport = (props: { title: string }) => {
    useSetPageTitle(props.title);
    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.usersImport")}</H1>
        </div>
        <div>
            <FileInput variant={"primary"} />
        </div>
    </>;
};

export default UsersImport;
