import React from "react";
import {Button, H1, MailHref, Searchbar, Table} from "@cloudogu/ces-theme-tailwind";
import {useUsers} from "../../hooks/useUsers";
import {t} from "../../helpers/i18nHelpers";
import {useSetPageTitle} from "../../hooks/useSetPageTitle";
import {useFilter} from "../../hooks/useFilter";
import {User, UsersService} from "../../services/Users";
import {useCasUser} from "../../hooks/useCasUser";
import {DeleteButton, EditButton} from "../../components/DeleteButton";
import {useChangeNotification} from "../../hooks/useChangeNotification";
import {useConfirmation} from "../../hooks/useConfirmation";
import {ConfirmationDialog} from "../../components/ConfirmationDialog";
import {CasUser} from "../../services/CasUser";

const FIRST_PAGE = 1;

export default function Users(props: { title: string }) {
    useSetPageTitle(props.title)
    const [setQuery, setPage, refetch, opts] = useFilter();
    const [usersModel, isLoading] = useUsers(opts)
    const [casUser] = useCasUser();
    const [notification, success, error] = useChangeNotification();
    const [open, toggleModal, username, setUsername] = useConfirmation();

    const changePage = (selectedPage: number) => {
        setPage(selectedPage)
    };
    const onSearch = (query: string) => {
        setQuery(query);
    };

    const updatePage = () => (usersModel?.users.length ?? 0) === 1
        && setPage(Math.max((usersModel?.pagination.current ?? 2) - 1, FIRST_PAGE))
        || refetch();

    const openConfirmationDialog = (groupName: string): void => {
        toggleModal(true);
        setUsername(groupName);
    }
    const onDelete = async (username: string) => {
        try {
            await UsersService.delete(username);
            updatePage();
            success(t("users.notification.success", {username: username}));
        } catch (e) {
            updatePage();
            error(t("users.notification.error", {username: username}));
        }
        toggleModal(false);
    }

    return <>
        <div className="flex justify-between">
            <H1 className="uppercase">{t("pages.users")}</H1>
            <div className="flex justify-between py-1">
                <Button variant={"secondary"} className="mt-5 mb-2.5 mr-5"
                        disabled={isLoading}>{t("users.create")}</Button>
                <Searchbar placeholder={"Filter"} clearOnSearch={false} onSearch={onSearch}
                           onClear={() => setQuery("")} startValueSearch={opts.query}
                           className="mt-5 mb-2.5" disabled={isLoading}/>
            </div>
        </div>
        {notification}
        <ConfirmationDialog open={open ?? false}
                            onClose={() => toggleModal(false)}
                            onConfirm={async () => {
                                await onDelete(username ?? "")
                            }}
                            title={t("users.confirmation.title")}
                            message={t("users.confirmation.message", {username: username})}/>
        <Table className="my-4 text-sm">
            <Table.Head>
                <Table.Head.Tr className={"uppercase"}>
                    <Table.Head.Th>{t("users.table.username")}</Table.Head.Th>
                    <Table.Head.Th>{t("users.table.displayName")}</Table.Head.Th>
                    <Table.Head.Th>{t("users.table.email")}</Table.Head.Th>
                    <Table.Head.Th className="w-0"></Table.Head.Th>
                </Table.Head.Tr>
            </Table.Head>
            <Table.ConditionalBody show={!isLoading}>
                {(usersModel.users ?? []).map(user => createUsersRow(user, casUser, openConfirmationDialog))}
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

function createUsersRow(user: User, casUser: CasUser, onDelete: (userName: string) => void) {
    return <Table.Body.Tr key={user.username}>
        <Table.Body.Td className="font-bold">{user.username}</Table.Body.Td>
        <Table.Body.Td className={"break-all"}>{user.displayName}</Table.Body.Td>
        <Table.Body.Td>
            <MailHref mail={user.mail}/>
        </Table.Body.Td>
        <Table.Body.Td className="flex justify-center">
            <EditButton title={t("users.table.actions.edit")}/>
            <DeleteButton
                disabled={user.username === casUser.principal}
                title={t("users.table.actions.delete")}
                onClick={() => onDelete(user.username)}/>
        </Table.Body.Td>
    </Table.Body.Tr>;
}