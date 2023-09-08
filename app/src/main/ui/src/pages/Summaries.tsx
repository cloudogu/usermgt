import {H1, useAlertNotification} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import SummaryList from "../components/summaries/SummaryList";
import {t} from "../helpers/i18nHelpers";
import {usePaginatedData} from "../hooks/usePaginatedData";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import {ImportUsersService} from "../services/ImportUsers";
import type {ImportSummary} from "../services/ImportUsers";

const Summaries = (props: { title: string }) => {
    const {data, setPage, refetch} = usePaginatedData<ImportSummary[]>(ImportUsersService.listSummaries, {pageSize: 10});
    const {notification, notify} = useAlertNotification();
    useSetPageTitle(props.title);

    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.summaries")}</H1>
        </div>
        <div className={"mb-2"}>
            {notification}
        </div>
        <SummaryList
            summaries={data.value || []}
            pageCount={data.pageCount}
            currentPage={data.currentPage}
            onPageChange={setPage}
            isLoading={data.isLoading}
            notify={notify}
            refetch={refetch}
        />
    </>;
};

export default Summaries;
