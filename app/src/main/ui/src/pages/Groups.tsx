import {ActionTable, ActionTableRoot, ConfirmDialog, translate,} from "@cloudogu/ces-theme-tailwind";
import {Button, H1, Searchbar, useAlertNotification} from "@cloudogu/deprecated-ces-theme-tailwind";
import React , {useMemo}from "react";
import {useLocation, useNavigate} from "react-router-dom";
import {DeleteButton} from "../components/DeleteButton";
import EditLink from "../components/EditLink";
import {t} from "../helpers/i18nHelpers";
import {useNotificationAfterRedirect} from "../hooks/useNotificationAfterRedirect";
import usePaginationTableState from "../hooks/usePaginationTableState";
import {GroupsService} from "../services/Groups";
import type {Group} from "../services/Groups";


export default function Groups() {
    const location = useLocation();
    const navigate = useNavigate();
    const {notification, notify} = useAlertNotification();
    useNotificationAfterRedirect(notify);
    const backUrlParams = useMemo((): string => {
        const backURL = `/groups${location.search}`;
        const params = new URLSearchParams();
        params.set("backURL", backURL);
        return params.toString();
    }, [location]);

    const {items, isLoading, paginationControl, updateSearchQuery, searchQuery, onDelete} = usePaginationTableState<Group>(GroupsService);

    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.groups")}</H1>
            <div className="flex flex-wrap justify-between py-1">
                <Button
                    variant={"secondary"} className="mt-5 mb-2.5 mr-5"
                    data-testid="group-create"
                    disabled={isLoading} onClick={() => navigate("/groups/new")}>
                    {t("groups.buttons.create")}</Button>
                <Searchbar
                    placeholder={"Filter"} clearOnSearch={false} onSearch={updateSearchQuery}
                    onClear={() => updateSearchQuery("")} startValueSearch={searchQuery}
                    data-testid="groups-filter" className="mt-5 mb-2.5" disabled={isLoading}/>
            </div>
        </div>
        {notification}
        <ActionTableRoot paginationControl={paginationControl} isLoading={isLoading}>
            <ActionTable className={"mt-default-2x"}>
                <ActionTable.HeadWithOneRow>
                    <ActionTable.HeadWithOneRow.Column>{t("groups.table.name")}</ActionTable.HeadWithOneRow.Column>
                    <ActionTable.HeadWithOneRow.Column>{t("groups.table.description")}</ActionTable.HeadWithOneRow.Column>
                    <ActionTable.HeadWithOneRow.Column>{t("groups.table.users")}</ActionTable.HeadWithOneRow.Column>
                    <ActionTable.HeadWithOneRow.Column
                        align={"center"}>{t("users.table.actions")}</ActionTable.HeadWithOneRow.Column>
                </ActionTable.HeadWithOneRow>
                {isLoading &&
                    <ActionTable.SkeletonBody columns={4} rows={20}/>
                }
                {!isLoading &&
                    <ActionTable.Body>
                        {items.map(group => (
                            <ActionTable.Body.Row key={group.name}>
                                <ActionTable.Body.Row.Column className="font-bold break-all">
                                    {group.name}
                                </ActionTable.Body.Row.Column>
                                <ActionTable.Body.Row.Column className={"break-all"}>
                                    <p>{group.description}</p>
                                    {group.isSystemGroup ?
                                        <p className="font-bold">{t("groups.table.systemGroup")}</p> : ""}
                                </ActionTable.Body.Row.Column>
                                <ActionTable.Body.Row.Column>
                                    <span className="flex justify-center">
                                        {group.members?.length ?? 0}
                                    </span>
                                </ActionTable.Body.Row.Column>
                                <ActionTable.Body.Row.Column className="flex justify-center">
                                    <EditLink
                                        className={"flex items-center"}
                                        to={`/groups/${group?.name ?? ""}/edit?${backUrlParams}`}
                                        id={`${group?.name}-edit-link`}/>
                                    <ConfirmDialog
                                        variant={"danger"}
                                        dialogBody={translate("groups.confirmation.message", {groupName: group.name})}
                                        dialogTitle={translate("groups.confirmation.title")}
                                        onConfirm={() => onDelete(group.name)}
                                        hasCancel
                                    >
                                        <DeleteButton
                                            id={`${group?.name}-delete-button`}
                                            disabled={group.isSystemGroup}
                                            title={t("groups.table.actions.delete")}
                                        />
                                    </ConfirmDialog>
                                </ActionTable.Body.Row.Column>
                            </ActionTable.Body.Row>
                        ))}
                    </ActionTable.Body>
                }
            </ActionTable>
        </ActionTableRoot>
    </>;
}
