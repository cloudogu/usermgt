import {ActionTable, ActionTableRoot, ConfirmDialog, DropdownMenu, translate} from "@cloudogu/ces-theme-tailwind";
import {CesIcons, TextWithIcon} from "@cloudogu/deprecated-ces-theme-tailwind";
import React from "react";
import {useTranslation} from "react-i18next";
import {t} from "../../helpers/i18nHelpers";
import usePaginationTableState from "../../hooks/usePaginationTableState";
import {ImportUsersService} from "../../services/ImportUsers";
import type {ImportSummary} from "../../services/ImportUsers";
import type {NotifyFunction} from "@cloudogu/deprecated-ces-theme-tailwind";

export interface SummaryListProps {
    notify: NotifyFunction;
}

export default function SummariesTable(
    {notify}: SummaryListProps) {
    const {i18n: {language}} = useTranslation();
    const {items: summaries, isLoading, paginationControl, onDelete} = usePaginationTableState<ImportSummary>(ImportUsersService);

    return (
        <>
            <ActionTableRoot paginationControl={paginationControl} isLoading={isLoading}>
                <ActionTable className={"mt-default-2x"}>
                    <ActionTable.HeadWithOneRow>
                        <ActionTable.HeadWithOneRow.Column>
                            {t("summaries.table.headline.name")}
                        </ActionTable.HeadWithOneRow.Column>
                        <ActionTable.HeadWithOneRow.Column>
                            {t("summaries.table.headline.date")}
                        </ActionTable.HeadWithOneRow.Column>
                        <ActionTable.HeadWithOneRow.Column>
                            {t("summaries.table.headline.result")}
                        </ActionTable.HeadWithOneRow.Column>
                        <ActionTable.HeadWithOneRow.Column>
                            {t("summaries.table.headline.functions")}
                        </ActionTable.HeadWithOneRow.Column>
                    </ActionTable.HeadWithOneRow>
                    {isLoading &&
                        <ActionTable.SkeletonBody columns={4} rows={10}/>
                    }
                    {!isLoading &&
                        <ActionTable.Body>
                            {
                                summaries.map((s) => (
                                    <ActionTable.Body.Row key={s.importID}>
                                        <ActionTable.Body.Row.Column>
                                            {s.filename}
                                        </ActionTable.Body.Row.Column>
                                        <ActionTable.Body.Row.Column>
                                            {s.timestamp.toLocaleString(language)}
                                        </ActionTable.Body.Row.Column>
                                        <ActionTable.Body.Row.Column>
                                            {t("summaries.result.created")}: {s.summary.created}{", "}
                                            {t("summaries.result.updated")}: {s.summary.updated}{", "}
                                            {t("summaries.result.errors")}: {s.summary.skipped}
                                        </ActionTable.Body.Row.Column>
                                        <ActionTable.Body.Row.Column>
                                            <DropdownMenu>
                                                <DropdownMenu.LinkItem
                                                    href={ImportUsersService.createDownloadLink(s)}
                                                >
                                                    <TextWithIcon icon={<CesIcons.DownloadSimple weight={"bold"}/>}>
                                                        {t("summaries.table.function.download")}
                                                    </TextWithIcon>
                                                </DropdownMenu.LinkItem>
                                                <DropdownMenu.RouterLinkItem
                                                    to={`/users/import/${s.importID}`}
                                                    state={{}}>
                                                    <TextWithIcon icon={<CesIcons.Table weight={"bold"}/>}>
                                                        {t("summaries.table.function.details")}
                                                    </TextWithIcon>
                                                </DropdownMenu.RouterLinkItem>
                                                <ConfirmDialog
                                                    variant={"danger"}
                                                    dialogBody={translate("summaries.delete.confirmation.message", {summaryId: s.filename})}
                                                    dialogTitle={translate("summaries.delete.confirmation.title")}
                                                    onConfirm={
                                                        () => onDelete(s.importID)
                                                            .then(() => notify(t("summaries.delete.success"), "primary"))
                                                            .catch(() => notify(t("summaries.delete.error"), "danger"))
                                                    }
                                                    hasCancel
                                                >
                                                    <DropdownMenu.Item onSelect={(e) => e.preventDefault()}>
                                                        <button>
                                                            <TextWithIcon icon={<CesIcons.TrashSimple weight={"bold"}/>}>
                                                                {t("summaries.table.function.delete")}
                                                            </TextWithIcon>
                                                        </button>
                                                    </DropdownMenu.Item>
                                                </ConfirmDialog>
                                            </DropdownMenu>
                                        </ActionTable.Body.Row.Column>
                                    </ActionTable.Body.Row>
                                ))
                            }
                        </ActionTable.Body>
                    }
                </ActionTable>
            </ActionTableRoot>
        </>
    );
}
