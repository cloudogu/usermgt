import {ActionTable, ActionTableRoot, ConfirmDialog, translate, useActualLocation,} from "@cloudogu/ces-theme-tailwind";
import {Button, H1, Searchbar, useAlertNotification} from "@cloudogu/deprecated-ces-theme-tailwind";
import React, {useContext, useEffect, useMemo} from "react";
import {Link, useSearchParams} from "react-router-dom";
import {DeleteButton} from "../components/DeleteButton";
import EditLink from "../components/EditLink";
import {t} from "../helpers/i18nHelpers";
import {useNotificationAfterRedirect} from "../hooks/useNotificationAfterRedirect";
import {ApplicationContext} from "../main";
import useUserTableState, {LINE_COUNT_OPTIONS} from "../hooks/useUserTableState";
import {LINES_PER_PAGE_QUERY_PARAM, PAGE_QUERY_PARAM, SEARCH_QUERY_PARAM} from "../hooks/usePaginatedData";

export default function Users() {
    const {casUser} = useContext(ApplicationContext);
    const location = useActualLocation();
    const [searchParams, setSearchParams] = useSearchParams();
    const {notification, notify, clearNotification} = useAlertNotification();
    useNotificationAfterRedirect(notify);
    const backUrlParams = useMemo((): string => {
        const backURL = `/users${location.search}`;
        const params = new URLSearchParams();
        params.set("backURL", backURL);
        return params.toString();
    }, [location]);

    const defaultLinesPerPage = useMemo(() => Number(searchParams.get(LINES_PER_PAGE_QUERY_PARAM) ?? 25), [JSON.stringify(searchParams.values)]);
    const defaultStartPage = useMemo(() => Number(searchParams.get(PAGE_QUERY_PARAM) ?? 1), [JSON.stringify(searchParams.values)]);
    const defaultSearchQuery = useMemo(() => searchParams.get(SEARCH_QUERY_PARAM) ?? "", [JSON.stringify(searchParams.values)]);

    const {users, isLoading, paginationControl, updateSearchQuery, searchQuery, onDelete} = useUserTableState({
        initialSearchQuery: defaultSearchQuery,
        defaultLinesPerPage: defaultLinesPerPage,
        defaultStartPage: defaultStartPage,
    });

    useEffect(() => {
        if (!LINE_COUNT_OPTIONS.includes(defaultLinesPerPage)) {
            if (!searchParams.get(LINES_PER_PAGE_QUERY_PARAM)){
                setSearchParams(current => {
                    current.set(LINES_PER_PAGE_QUERY_PARAM, `${25}`);
                    return current;
                });
            }
        }
        if (defaultStartPage !== paginationControl.page) {
            if (!searchParams.get(LINES_PER_PAGE_QUERY_PARAM)){
                setSearchParams(current => {
                    current.set(LINES_PER_PAGE_QUERY_PARAM, `${paginationControl.page}`);
                    return current;
                });
            }
        }
    }, [JSON.stringify(searchParams.values), paginationControl.page, setSearchParams]);

    return <>
        <div className="flex flex-wrap justify-between">
            <H1 className="uppercase">{t("pages.users")}</H1>
            <div className="flex flex-wrap justify-between py-1">
                <Link to={"/users/new"}>
                    <Button
                        variant={"secondary"}
                        className="mt-5 mb-2.5 mr-5"
                        data-testid="user-create"
                        disabled={isLoading}>{t("users.create")}
                    </Button>
                </Link>
                <Searchbar
                    placeholder={"Filter"}
                    clearOnSearch={false}
                    onSearch={(v) => updateSearchQuery(v)}
                    onClear={() => updateSearchQuery("")}
                    startValueSearch={searchQuery}
                    data-testid="users-filter" className="mt-5 mb-2.5" disabled={isLoading}
                />
            </div>
        </div>
        {notification}
        <ActionTableRoot paginationControl={paginationControl} isLoading={isLoading}>
            <ActionTable className={"mt-default-2x"}>
                <ActionTable.HeadWithOneRow>
                    <ActionTable.HeadWithOneRow.Column>{t("users.table.username")}</ActionTable.HeadWithOneRow.Column>
                    <ActionTable.HeadWithOneRow.Column>{t("users.table.displayName")}</ActionTable.HeadWithOneRow.Column>
                    <ActionTable.HeadWithOneRow.Column>{t("users.table.email")}</ActionTable.HeadWithOneRow.Column>
                    <ActionTable.HeadWithOneRow.Column
                        align={"center"}>{t("users.table.actions")}</ActionTable.HeadWithOneRow.Column>
                </ActionTable.HeadWithOneRow>
                {isLoading &&
                    <ActionTable.SkeletonBody columns={4} rows={20}/>
                }
                {!isLoading &&
                    <ActionTable.Body>
                        {users.map(user => (
                            <ActionTable.Body.Row key={user.username}>
                                <ActionTable.Body.Row.Column className="font-bold break-all">
                                    {user.username}
                                </ActionTable.Body.Row.Column>
                                <ActionTable.Body.Row.Column className={"break-all"}>
                                    {user.displayName}
                                </ActionTable.Body.Row.Column>
                                <ActionTable.Body.Row.Column>
                                    <a href={`mailto:${user.mail}`}/>
                                </ActionTable.Body.Row.Column>
                                <ActionTable.Body.Row.Column className="flex justify-center">
                                    <EditLink
                                        className={"flex items-center"}
                                        to={`/users/${user?.username ?? ""}/edit?${backUrlParams}`}
                                        id={`${user?.username}-edit-link`}/>
                                    <ConfirmDialog
                                        variant={"danger"}
                                        dialogBody={translate("users.confirmation.message", {username: user.username})}
                                        dialogTitle={translate("users.confirmation.title")}
                                        onConfirm={() => onDelete(user.username)}
                                        hasCancel
                                    >
                                        <DeleteButton
                                            id={`${user?.username}-delete-button`}
                                            disabled={user.username === casUser.principal}
                                            title={t("users.table.actions.delete")}
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