import {H1} from "@cloudogu/ces-theme-tailwind";
import React, {useState} from "react";
import {t} from "../helpers/i18nHelpers";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import useProtocolList from "../hooks/useProtocolList";

const UsersImportResult = (props: { title: string }) => {
    const [test, setTest] = useState({limit: 0, query: "", start: 0});
    const {data, isLoading} = useProtocolList(test);
    useSetPageTitle(props.title);

    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.importProtocols")}</H1>
        </div>

        {data?.protocols?.length}
        {isLoading ? "TRUE" : "FALSE"}
    </>;
};

export default UsersImportResult;
