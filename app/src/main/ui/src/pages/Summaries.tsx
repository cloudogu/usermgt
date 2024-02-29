import {H1, useAlertNotification} from "@cloudogu/deprecated-ces-theme-tailwind";
import React from "react";
import SummariesTable from "../components/summaries/SummariesTable";
import {t} from "../helpers/i18nHelpers";

const Summaries = () => {
    const {notification, notify} = useAlertNotification();

    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.summaries")}</H1>
        </div>
        <div className={"mb-2"}>
            {notification}
        </div>
        <SummariesTable notify={notify}/>
    </>;
};

export default Summaries;
