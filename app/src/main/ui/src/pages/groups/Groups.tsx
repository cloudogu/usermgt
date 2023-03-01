import React from "react";
import {Button, H1, LoadingIcon, Searchbar, Table} from "@cloudogu/ces-theme-tailwind";
import {useGroups} from "../../hooks/useGroups";
import {t} from "../../helpers/i18nHelpers";
import {Group, GroupsService} from "../../services/Groups";
import {useFilter} from "../../hooks/useFilter";
import {useSetPageTitle} from "../../hooks/useSetPageTitle";
import {DeleteButton, EditButton} from "../../components/DeleteButton";
import {useChangeNotification} from "../../hooks/useChangeNotification";
import {ConfirmationDialog} from "../../components/ConfirmationDialog";
import {useConfirmation} from "../../hooks/useConfirmation";

const FIRST_PAGE = 1;

export default function Groups(props: { title: string }) {
    useSetPageTitle(props.title)
    const [setQuery, setPage, refetch, opts] = useFilter();
    const [groupsModel, isLoading] = useGroups(opts);
    const [notification, success, error] = useChangeNotification();
    const [open, toggleModal, group, setGroup] = useConfirmation();

    const changePage = (selectedPage: number) => {
        setPage(selectedPage)
    };
    const onSearch = (query: string) => {
        setQuery(query);
    };
    const updatePage = () => (groupsModel?.groups.length ?? 0) === 1
        && setPage(Math.max((groupsModel?.pagination.current ?? 2) - 1, FIRST_PAGE))
        || refetch();

    const openConfirmationDialog = (groupName: string): void => {
        toggleModal(true);
        setGroup(groupName);
    }
    const onDelete = async (groupName: string) => {
        try {
            await GroupsService.delete(groupName);
            updatePage();
            success(t("groups.notification.success", {groupName: groupName}));
        } catch (e) {
            updatePage();
            error(t("groups.notification.error", {groupName: groupName}));
        }
        toggleModal(false);
    }
    return <>
        <div className="flex justify-between">
            <H1 className="uppercase">{t("pages.groups")}</H1>
            <div className="flex justify-between py-1">
                <Button variant={"secondary"} className="mt-5 mb-2.5 mr-5"
                        disabled={isLoading}>{t("groups.create")}</Button>
                <Searchbar placeholder={"Filter"} clearOnSearch={false} onSearch={onSearch}
                           onClear={() => setQuery("")} startValueSearch={opts.queryString}
                           className="mt-5 mb-2.5" disabled={isLoading}/>
            </div>
        </div>
        {notification}
        <ConfirmationDialog open={open ?? false}
                            onClose={() => toggleModal(false)}
                            onConfirm={async () => {
                                await onDelete(group ?? "")
                            }}
                            className="-relative z-[51] sm:w-3/4 md:w-1/2"
                            title={t("groups.confirmation.title")}
                            message={t("groups.confirmation.message", {groupName: group})}/>

        <Table className="mt-4 text-sm">
            <Table.Head key={"table-head"}>
                <Table.Head.Tr className={"uppercase"}>
                    <Table.Head.Th>{t("groups.table.name")}</Table.Head.Th>
                    <Table.Head.Th>{t("groups.table.description")}</Table.Head.Th>
                    <Table.Head.Th>{t("groups.table.users")}</Table.Head.Th>
                    <Table.Head.Th className="w-0"></Table.Head.Th>
                </Table.Head.Tr>
            </Table.Head>
            {isLoading ?
                <Table.Body>
                    <Table.Body.Tr>
                        <Table.Body.Td colSpan={4}>
                            <div className={"flex justify-center w-full mt-4"}>
                                <LoadingIcon className={"w-64 h-64"}/>
                            </div>
                        </Table.Body.Td>
                    </Table.Body.Tr>
                </Table.Body>
                :
                <>
                    <Table.Body>
                        {groupsModel?.groups?.map(group => createGroupRow(group, openConfirmationDialog))}
                    </Table.Body>
                    <Table.Foot>
                        <Table.Foot.Pagination
                            currentPage={groupsModel?.pagination.current ?? 1}
                            pageCount={groupsModel?.pagination.pageCount ?? 1}
                            onPageChange={changePage}/>
                    </Table.Foot>
                </>
            }
        </Table>
    </>;
}

function createGroupRow(group: Group, onDelete: (groupName: string) => void) {
    return <Table.Body.Tr key={group.name}>
        <Table.Body.Td>
            <span className="font-bold">{group.name}</span>
        </Table.Body.Td>
        <Table.Body.Td>
            <p>{group.description}</p>
            {group.isSystemGroup ?
                <p className="font-bold">{t("groups.table.systemGroup")}</p> : ""}
        </Table.Body.Td>
        <Table.Body.Td>
            <span className="flex justify-center w-full">
                {group.members?.length ?? 0}
            </span>
        </Table.Body.Td>
        <Table.Body.Td className="flex justify-center">
            <EditButton aria-label={t("groups.table.actions.editAria")}
                        title={t("groups.table.actions.edit")}/>
            <DeleteButton aria-label={t("groups.table.actions.deleteAria")} disabled={group.isSystemGroup}
                          title={t("groups.table.actions.delete")}
                          onClick={() => onDelete(group.name)}/>
        </Table.Body.Td>
    </Table.Body.Tr>
}