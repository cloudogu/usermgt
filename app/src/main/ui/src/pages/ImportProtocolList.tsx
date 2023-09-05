import {H1} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import SummaryList from "../components/importProtocolList/SummaryList";
import {t} from "../helpers/i18nHelpers";
import {useFilter} from "../hooks/useFilter";
import useProtocolList from "../hooks/useProtocolList";
import {useSetPageTitle} from "../hooks/useSetPageTitle";

const UsersImportResult = (props: { title: string }) => {
    const {opts, updatePage} = useFilter();
    const {data} = useProtocolList(opts);
    useSetPageTitle(props.title);

    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.importProtocols")}</H1>
        </div>
        <SummaryList
            protocols={data?.summaries || []}
            pageCount={data?.pagination?.pageCount ?? 1}
            currentPage={data?.pagination?.current ?? 1}
            onPageChange={updatePage}
        />
    </>;
};

export default UsersImportResult;
