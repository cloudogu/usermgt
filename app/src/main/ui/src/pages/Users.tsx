import {Button, H1, MailHref, Searchbar, Table, useAlertNotification} from "@cloudogu/ces-theme-tailwind";
import React, {useContext} from "react";
import {ConfirmationDialog} from "../components/ConfirmationDialog";
import {DeleteButton} from "../components/DeleteButton";
import {t} from "../helpers/i18nHelpers";
import {useConfirmation} from "../hooks/useConfirmation";
import {useFilter} from "../hooks/useFilter";
import {useNotificationAfterRedirect} from "../hooks/useNotificationAfterRedirect";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import {useUsers} from "../hooks/useUsers";
import {ApplicationContext} from "../main";
import type {User} from "../services/Users";
import {UsersService} from "../services/Users";
import type {CasUser} from "../services/CasUser";
import EditLink from "../components/EditLink";
import {Link, useLocation} from "react-router-dom";

const FIRST_PAGE = 1;


export default function Users(props: { title: string }) {
    const location = useLocation();
    const {updateQuery, updatePage, refetch, opts} = useFilter();
    const {users: usersModel, isLoading} = useUsers(opts);
    const {casUser} = useContext(ApplicationContext);
    const {notification, notify, clearNotification} = useAlertNotification();
    useNotificationAfterRedirect(notify);
    useSetPageTitle(props.title);
    const {open, setOpen: toggleModal, targetName: username, setTargetName: setUsername} = useConfirmation();
    const backUrlParams = (): string => {
        const backURL = `/users${location.search}`;
        const params = new URLSearchParams();
        params.set("backURL", backURL);
        return params.toString();
    }

    const changePage = (selectedPage: number) => {
        clearNotification();
        updatePage(selectedPage);
    };
    const onSearch = (query: string) => {
        clearNotification();
        updateQuery(query);
    };

    const reloadPage = () => (usersModel?.users.length ?? 0) === 1
        && updatePage(Math.max((usersModel?.pagination.current ?? 2) - 1, FIRST_PAGE))
        || refetch();

    const openConfirmationDialog = (groupName: string): void => {
        toggleModal(true);
        setUsername(groupName);
    };
    const onDelete = async (username: string) => {
        try {
            await UsersService.delete(username);
            reloadPage();
            notify(t("users.notification.success", {username: username}), "primary");
        } catch (e) {
            reloadPage();
            notify(t("users.notification.error", {username: username}), "danger");
        }
        toggleModal(false);
    };

    return <>
        <div className="flex justify-between">
            <H1 className="uppercase">{t("pages.users")}</H1>
            <div className="flex justify-between py-1">
                <Link to={"/users/new"}>
                    <Button variant={"secondary"} className="mt-5 mb-2.5 mr-5"
                            disabled={isLoading}>{t("users.create")}
                    </Button>
                </Link>
                <Searchbar placeholder={"Filter"} clearOnSearch={false} onSearch={onSearch}
                           onClear={() => updateQuery("")} startValueSearch={opts.query}
                           className="mt-5 mb-2.5" disabled={isLoading}/>
            </div>
        </div>
        {notification}
        <ConfirmationDialog open={open ?? false}
                            onClose={() => toggleModal(false)}
                            onConfirm={async () => {
                                await onDelete(username ?? "");
                            }}
                            title={t("users.confirmation.title")}
                            message={t("users.confirmation.message", {username: username})}/>
        <Table className="my-4 text-sm">
            <Table.Head>
                <Table.Head.Tr className={"uppercase"}>
                    <Table.Head.Th>{t("users.table.username")}</Table.Head.Th>
                    <Table.Head.Th>{t("users.table.displayName")}</Table.Head.Th>
                    <Table.Head.Th>{t("users.table.email")}</Table.Head.Th>
                    <Table.Head.Th className="w-0"/>
                </Table.Head.Tr>
            </Table.Head>
            <Table.ConditionalBody show={!isLoading}>
                {(usersModel.users ?? []).map(user => createUsersRow(user, casUser, openConfirmationDialog, backUrlParams()))}
            </Table.ConditionalBody>
            <Table.ConditionalFoot show={!isLoading && usersModel.users?.length > 0}>
                <Table.Foot.Pagination
                    className={"fixed bottom-4 left-1/2 -translate-x-1/2"}
                    currentPage={usersModel.pagination.current ?? 1}
                    pageCount={usersModel.pagination.pageCount ?? 1}
                    onPageChange={changePage}/>
            </Table.ConditionalFoot>
        </Table>
    </>;
}

function createUsersRow(user: User, casUser: CasUser, onDelete: (_: string) => void, backUrlParams: string) {
    return (
        <Table.Body.Tr key={user.username}>
            <Table.Body.Td className="font-bold">{user.username}</Table.Body.Td>
            <Table.Body.Td className={"break-all"}>{user.displayName}</Table.Body.Td>
            <Table.Body.Td>
                <MailHref mail={user.mail}/>
            </Table.Body.Td>
            <Table.Body.Td className="flex justify-center">
                <EditLink to={`/users/${user?.username ?? ""}/edit?${backUrlParams}`}></EditLink>
                <DeleteButton
                    disabled={user.username === casUser.principal}
                    title={t("users.table.actions.delete")}
                    onClick={() => onDelete(user.username)}/>
            </Table.Body.Td>
        </Table.Body.Tr>
    );
}