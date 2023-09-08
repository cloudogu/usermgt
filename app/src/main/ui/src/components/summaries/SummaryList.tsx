import {CesIcons, DropdownMenu, Table, TextWithIcon} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {t} from "../../helpers/i18nHelpers";
import {ImportUsersService} from "../../services/ImportUsers";
import type {ImportSummary} from "../../services/ImportUsers";
import type {NotifyFunction} from "@cloudogu/ces-theme-tailwind";

export interface SummaryListProps {
    summaries: ImportSummary[],
    pageCount: number;
    currentPage: number;
    onPageChange: (_newPage: number) => void;
    isLoading: boolean;
    notify: NotifyFunction;
    refetch: () => void;
}

export default function SummaryList(
    {
        notify,
        summaries,
        pageCount,
        currentPage,
        onPageChange,
        isLoading,
        refetch,
    }: SummaryListProps) {
    const language = navigator?.language ?? "de-DE";

    return (
        <Table>
            <Table.Head>
                <Table.Head.Tr>
                    <Table.Head.Th>
                        {t("summaries.table.headline.name")}
                    </Table.Head.Th>
                    <Table.Head.Th>
                        {t("summaries.table.headline.date")}
                    </Table.Head.Th>
                    <Table.Head.Th>
                        {t("summaries.table.headline.result")}
                    </Table.Head.Th>
                    <Table.Head.Th>
                        {t("summaries.table.headline.functions")}
                    </Table.Head.Th>
                </Table.Head.Tr>
            </Table.Head>
            <Table.ConditionalBody show={!isLoading}>
                {
                    summaries.map((s, i) => (
                        <Table.Body.Tr key={i}>
                            <Table.Body.Td>
                                {s.filename}
                            </Table.Body.Td>
                            <Table.Body.Td>
                                {s.timestamp.toLocaleString(language)}
                            </Table.Body.Td>
                            <Table.Body.Td>
                                {t("summaries.result.created")}: {s.summary.created}{", "}
                                {t("summaries.result.updated")}: {s.summary.updated}{", "}
                                {t("summaries.result.errors")}: {s.summary.skipped}
                            </Table.Body.Td>
                            <Table.Body.Td className={"flex flex-row"}>
                                <DropdownMenu
                                    aria-label={t("summaries.aria.label.dropdown")}
                                    aria-description={t("summaries.aria.description.dropdown")}
                                >
                                    <DropdownMenu.Button
                                        aria-label={t("summaries.aria.label.dropdownOpen")}
                                        aria-description={t("summaries.aria.description.dropdownOpen")}
                                    >
                                        {t("summaries.table.functions")}
                                        <DropdownMenu.Button.Arrow/>
                                    </DropdownMenu.Button>
                                    <DropdownMenu.Items>
                                        <DropdownMenu.Items.LinkItem
                                            aria-label={t("summaries.aria.label.dropdownDownload")}
                                            aria-description={t("summaries.aria.description.dropdownDownload")}
                                            href={`/usermgt/api/users/import/${s.importID}/download`}
                                            className={"flex flex-row"}
                                        >
                                            <TextWithIcon icon={<CesIcons.DownloadSimple weight={"bold"}/>}>
                                                {t("summaries.table.function.download")}
                                            </TextWithIcon>
                                        </DropdownMenu.Items.LinkItem>
                                        <DropdownMenu.Items.RouterLinkItem
                                            aria-label={t("summaries.aria.label.dropdownDetails")}
                                            aria-description={t("summaries.aria.description.dropdownDetails")}
                                            to={`/users/import/${s.importID}`}
                                            className={"flex"}
                                            state={{}}>
                                            <TextWithIcon icon={<CesIcons.Table weight={"bold"}/>}>
                                                {t("summaries.table.function.details")}
                                            </TextWithIcon>
                                        </DropdownMenu.Items.RouterLinkItem>
                                        <DropdownMenu.Items.ButtonItem
                                            aria-label={t("summaries.aria.label.dropdownDelete")}
                                            aria-description={t("summaries.aria.description.dropdownDelete")}
                                            className={"flex flex-row"}
                                            onClick={
                                                () => ImportUsersService.deleteSummary(s)
                                                    .then(() => notify(t("summaries.delete.success"), "primary"))
                                                    .catch(() => notify(t("summaries.delete.error"), "danger"))
                                                    .finally(() => refetch())
                                            }
                                        >
                                            <TextWithIcon icon={<CesIcons.TrashSimple weight={"bold"}/>}>
                                                {t("summaries.table.function.delete")}
                                            </TextWithIcon>
                                        </DropdownMenu.Items.ButtonItem>
                                    </DropdownMenu.Items>
                                </DropdownMenu>
                            </Table.Body.Td>
                        </Table.Body.Tr>
                    ))
                }
            </Table.ConditionalBody>
            <Table.ConditionalFoot show={pageCount > 1}>
                <Table.Foot.Pagination pageCount={pageCount} currentPage={currentPage} onPageChange={onPageChange}/>
            </Table.ConditionalFoot>
        </Table>
    );
}