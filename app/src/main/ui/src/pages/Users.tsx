import {
    ActionTable,
    ActionTableRoot,
    ConfirmDialog,
    translate,
    useActionTableControl,
    useActualLocation
} from "@cloudogu/ces-theme-tailwind";
import {Button, H1, Searchbar, useAlertNotification} from "@cloudogu/deprecated-ces-theme-tailwind";
import React, {useContext, useMemo} from "react";
import {Link} from "react-router-dom";
import {DeleteButton} from "../components/DeleteButton";
import EditLink from "../components/EditLink";
import {t} from "../helpers/i18nHelpers";
import {useNotificationAfterRedirect} from "../hooks/useNotificationAfterRedirect";
import {ApplicationContext} from "../main";
import type {User} from "../services/Users";

// const FIRST_PAGE = 1;


export default function Users() {
    const {casUser} = useContext(ApplicationContext);
    const location = useActualLocation();
    const {notification, notify, clearNotification} = useAlertNotification();
    useNotificationAfterRedirect(notify);
    const backUrlParams = useMemo((): string => {
        const backURL = `/users${location.search}`;
        const params = new URLSearchParams();
        params.set("backURL", backURL);
        return params.toString();
    }, [location]);

    const {users, isLoading, allLineCount, loadDataFunction, updateSearchQuery, searchQuery, onDelete} = {
        users: [{
            displayName: "usera1",
            mail: "usera1",
            username: "usera1",
            external: true,
            givenname: "usera1",
            memberOf: [],
            password: "usera1",
            pwdReset: true,
            surname: "usera1",
        }] as User[],
        isLoading: false,
        allLineCount: 0,
        loadDataFunction: async () => {
            console.log("LOAD DATA...");
        },
        updateSearchQuery: (_: string) => {
            console.log(`setSearchQuery: ${_}`);
        },
        searchQuery: "",
        onDelete: async (_: string) => {
            console.log(`delete: ${_}`);
        },
    };

    const actionTableControl = useActionTableControl({
        isLoading: false,
        paginationControl: {
            defaultLinesPerPage: 15,
            lineCountOptions: [
                15,
                30,
                60,
                {value: -1, text: t("general.all")},
            ],
            allLineCount: allLineCount,
            loadDataFunction: loadDataFunction,
        },
    });

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
        <ActionTableRoot {...actionTableControl}>
            <ActionTable>
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