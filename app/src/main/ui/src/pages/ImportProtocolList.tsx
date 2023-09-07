import {H1} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import SummaryList from "../components/importProtocolList/SummaryList";
import {t} from "../helpers/i18nHelpers";
import {usePaginatedData} from "../hooks/usePaginatedData";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import {ImportUsersService} from "../services/ImportUsers";
import type {ImportSummary} from "../services/ImportUsers";

const UsersImportResult = (props: { title: string }) => {
    const {data, setPage} = usePaginatedData<ImportSummary[]>(ImportUsersService.listSummaries, {pageSize: 10});
    useSetPageTitle(props.title);

    // console.log(data);

    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.importProtocols")}</H1>
        </div>
        <SummaryList
            protocols={data.value || []}
            pageCount={data.pageCount}
            currentPage={data.currentPage}
            onPageChange={setPage}
        />
    </>;
};

export default UsersImportResult;
