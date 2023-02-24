import React from "react";
import {Button, H1, LoadingIcon, Searchbar, Table} from "@cloudogu/ces-theme-tailwind";
import {useUsers} from "../../hooks/useUsers";
import {t} from "../../helpers/i18nHelpers";
import {useSetPageTitle} from "../../hooks/useSetPageTitle";
import {useFilter} from "../../hooks/useFilter";
import {User, UsersService} from "../../services/Users";
import {useUser} from "../../hooks/useUser";
import {CasUser} from "../../api/CasUserAPI";
import {DeleteButton, EditButton} from "../../components/DeleteButton";
import {useChangeNotification} from "../../hooks/useChangeNotification";
import {useConfirmation} from "../../hooks/useConfirmation";
import {ConfirmationDialog} from "../../components/ConfirmationDialog";

export default function Users(props: { title: string }) {
    useSetPageTitle(props.title)
    const [setQuery, setPage, refetch, opts] = useFilter();
    const [usersModel, isLoading] = useUsers(opts)
    const [casUser] = useUser();
    const [notification, success, error] = useChangeNotification();
    const [open, setOpen, username, setUsername] = useConfirmation();
    const changePage = (selectedPage: number) => {
        setPage(selectedPage)
    };
    const onSearch = (query: string) => {
        setQuery(query);
    };

    const updatePage = () => (usersModel?.users.length ?? 0) === 1
        && setPage((usersModel?.pagination.current ?? 2) - 1)
        || refetch();

    const openConfirmationDialog = (groupName: string): void => {
        setOpen(true);
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
        setOpen(false);
    }

    return <>
        <div className="flex justify-between">
            <H1 className="uppercase">{t("pages.users")}</H1>
            <div className="flex justify-between py-1">
                <Button variant={"secondary"} className="mt-5 mb-2.5 mr-5"
                        disabled={isLoading}>{t("users.create")}</Button>
                <Searchbar placeholder={"Filter"} clearOnSearch={false} onSearch={onSearch}
                           className="mt-5 mb-2.5"  disabled={isLoading}/>
            </div>
        </div>
        {notification}
        <ConfirmationDialog open={open ?? false}
                            onClose={() => setOpen(false)}
                            onConfirm={async () => {
                                await onDelete(username ?? "")
                                setOpen(false);
                            }}
                            className="-relative z-[51] sm:w-3/4 md:w-1/2"
                            title={t("users.confirmation.title")}
                            message={t("users.confirmation.message", {username: username})}/>
        {isLoading ?
            <div className={"flex justify-center w-[100%] mt-4"}>
                <LoadingIcon className={"w-64 h-64"}/>
            </div>
            :
            (<Table className="mt-4 text-sm">
                <Table.Head>
                    <Table.Head.Tr className={"uppercase"}>
                        <Table.Head.Th>{t("users.table.username")}</Table.Head.Th>
                        <Table.Head.Th>{t("users.table.displayName")}</Table.Head.Th>
                        <Table.Head.Th>{t("users.table.email")}</Table.Head.Th>
                        <Table.Head.Th className="w-0"></Table.Head.Th>
                    </Table.Head.Tr>
                </Table.Head>
                <Table.Body>
                    {usersModel.users.map(user => createUsersRow(user, casUser, openConfirmationDialog))}
                </Table.Body>
                <Table.Foot>
                    <Table.Foot.Pagination
                        currentPage={usersModel.pagination.current ?? 1}
                        pageCount={usersModel.pagination.pageCount ?? 1}
                        onPageChange={changePage}/>
                </Table.Foot>
            </Table>)}
    </>;
}

function createUsersRow(user: User, casUser: CasUser, onDelete: (userName: string) => void) {
    return <Table.Body.Tr key={user.username}>
        <Table.Body.Td>
            <span className="font-bold">{user.username}</span>
        </Table.Body.Td>
        <Table.Body.Td>{user.displayName}</Table.Body.Td>
        <Table.Body.Td>
            <a className="hover:underline decoration-solid text-link-primary-font"
               href={"https://ecosystem.cloudogu.com/"}>
                {user.mail}
            </a>
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