import {
    Button,
    H1,
    MailHref,
    Searchbar,
    Table,
    useAlertNotification
} from "@cloudogu/ces-theme-tailwind";
import React, {useContext} from "react";
import {Link, useLocation} from "react-router-dom";
import {ConfirmationDialog} from "../components/ConfirmationDialog";
import {DeleteButton} from "../components/DeleteButton";
import EditLink from "../components/EditLink";
import {t} from "../helpers/i18nHelpers";
import {useConfirmation} from "../hooks/useConfirmation";
import {useNotificationAfterRedirect} from "../hooks/useNotificationAfterRedirect";
import useUsers from "../hooks/useUsers";
import {ApplicationContext} from "../main";
import {UsersService} from "../services/Users";
import type {CasUser} from "../services/CasUser";
import type {User} from "../services/Users";

const FIRST_PAGE = 1;


export default function Users() {
    const {casUser} = useContext(ApplicationContext);
    const location = useLocation();
    const {
        data: {value: users, isLoading, currentPage, pageCount},
        setPage: updatePage,
        setSearchString: updateQuery,
        refetch,
        opts,
    } = useUsers();
    const {notification, notify, clearNotification} = useAlertNotification();
    useNotificationAfterRedirect(notify);
    const {open, setOpen: toggleModal, targetName: username, setTargetName: setUsername} = useConfirmation();
    const backUrlParams = (): string => {
        const backURL = `/users${location.search}`;
        const params = new URLSearchParams();
        params.set("backURL", backURL);
        return params.toString();
    };

    const changePage = (selectedPage: number) => {
        clearNotification();
        updatePage(selectedPage);
    };
    const onSearch = (query: string) => {
        clearNotification();
        updateQuery(query);
    };

    const reloadPage = () => (users?.length ?? 0) === 1
        && updatePage(Math.max((currentPage ?? 2) - 1, FIRST_PAGE))
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
                    onSearch={onSearch}
                    onClear={() => updateQuery("")} startValueSearch={opts.query}
                    data-testid="users-filter" className="mt-5 mb-2.5" disabled={isLoading}/>
            </div>
        </div>
        {notification}
        <ConfirmationDialog
            open={open ?? false}
            data-testid="user-delete-dialog"
            onClose={() => toggleModal(false)}
            onConfirm={async () => {
                await onDelete(username ?? "");
            }}
            title={t("users.confirmation.title")}
            message={t("users.confirmation.message", {username: username})}/>
        <Table className="my-4 text-sm" data-testid="users-table">
            <Table.Head>
                <Table.Head.Tr className={"uppercase"}>
                    <Table.Head.Th>{t("users.table.username")}</Table.Head.Th>
                    <Table.Head.Th>{t("users.table.displayName")}</Table.Head.Th>
                    <Table.Head.Th>{t("users.table.email")}</Table.Head.Th>
                    <Table.Head.Th className="w-0"/>
                </Table.Head.Tr>
            </Table.Head>
            <Table.ConditionalBody show={!isLoading}>
                {(users ?? []).map(user => createUsersRow(user, casUser, openConfirmationDialog, backUrlParams()))}
            </Table.ConditionalBody>
            <Table.ConditionalFoot show={!isLoading && (pageCount ?? 1) > 1}>
                <Table.Foot.Pagination
                    data-testid="users-footer"
                    className={"fixed bottom-4 left-1/2 -translate-x-1/2"}
                    currentPage={currentPage ?? 1}
                    pageCount={pageCount ?? 1}
                    onPageChange={changePage}
                />
            </Table.ConditionalFoot>
        </Table>
    </>;
}

function createUsersRow(user: User, casUser: CasUser, onDelete: (_: string) => void, backUrlParams: string) {
    return (
        <Table.Body.Tr key={user.username}>
            <Table.Body.Td className="font-bold break-all">{user.username}</Table.Body.Td>
            <Table.Body.Td className={"break-all"}>{user.displayName}</Table.Body.Td>
            <Table.Body.Td>
                <MailHref mail={user.mail}/>
            </Table.Body.Td>
            <Table.Body.Td className="flex justify-center">
                <EditLink
                    to={`/users/${user?.username ?? ""}/edit?${backUrlParams}`}
                    id={`${user?.username}-edit-link`}/>
                <DeleteButton
                    id={`${user?.username}-delete-button`}
                    disabled={user.username === casUser.principal}
                    title={t("users.table.actions.delete")}
                    onClick={() => onDelete(user.username)}/>
            </Table.Body.Td>
        </Table.Body.Tr>
    );
}