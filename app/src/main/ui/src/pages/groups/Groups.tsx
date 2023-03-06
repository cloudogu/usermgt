import {Button, H1, Searchbar, Table} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {useNavigate} from "react-router-dom";
import {ConfirmationDialog} from "../../components/ConfirmationDialog";
import {DeleteButton, EditButton} from "../../components/DeleteButton";
import {t} from "../../helpers/i18nHelpers";
import {useChangeNotification} from "../../hooks/useChangeNotification";
import {useConfirmation} from "../../hooks/useConfirmation";
import {useFilter} from "../../hooks/useFilter";
import {useGroups} from "../../hooks/useGroups";
import {useNotificationAfterRedirect} from "../../hooks/useNotificationAfterRedirect";
import {useSetPageTitle} from "../../hooks/useSetPageTitle";
import { GroupsService} from "../../services/Groups";
import type {Group} from "../../services/Groups";

const FIRST_PAGE = 1;

export default function Groups(props: { title: string }) {
    const {updateQuery:setQuery, updatePage:setPage, refetch, opts} = useFilter();
    const {groups:model, isLoading} = useGroups(opts);
    const navigate = useNavigate();
    const {notification, notify, clearNotification} = useChangeNotification();
    useNotificationAfterRedirect(notify);
    useSetPageTitle(props.title);
    const {open, setOpen:toggleModal, targetName:group, setTargetName:setGroup} = useConfirmation();

    const changePage = (selectedPage: number) => {
        clearNotification();
        setPage(selectedPage);
    };
    const onSearch = (query: string) => {
        clearNotification();
        setQuery(query);
    };
    const updatePage = () => (model?.groups.length ?? 0) === 1
        && setPage(Math.max((model?.pagination.current ?? 2) - 1, FIRST_PAGE))
        || refetch();

    const openConfirmationDialog = (groupName: string): void => {
        toggleModal(true);
        setGroup(groupName);
    };
    const onDelete = async (groupName: string) => {
        try {
            await GroupsService.delete(groupName);
            updatePage();
            notify(t("groups.notification.success", {groupName: groupName}), "primary");
        } catch (e) {
            updatePage();
            notify(t("groups.notification.error", {groupName: groupName}), "danger");
        }
        toggleModal(false);
    };
    const editGroup = (groupName: string) => {
        navigate(`/groups/${groupName}/edit`);
    };
    return <>
        <div className="flex justify-between">
            <H1 className="uppercase">{t("pages.groups")}</H1>
            <div className="flex justify-between py-1">
                <Button variant={"secondary"} className="mt-5 mb-2.5 mr-5"
                    disabled={isLoading} onClick={() => navigate("/groups/new", {state: {name: "test"}})}>
                    {t("groups.create")}</Button>
                <Searchbar placeholder={"Filter"} clearOnSearch={false} onSearch={onSearch}
                    onClear={() => setQuery("")} startValueSearch={opts.query}
                    className="mt-5 mb-2.5" disabled={isLoading}/>
            </div>
        </div>
        {notification}
        <ConfirmationDialog open={open ?? false}
            onClose={() => toggleModal(false)}
            onConfirm={async () => {
                await onDelete(group ?? "");
            }}
            title={t("groups.confirmation.title")}
            message={t("groups.confirmation.message", {groupName: group})}/>

        <Table className="my-4">
            <Table.Head key={"table-head"}>
                <Table.Head.Tr className={"uppercase"}>
                    <Table.Head.Th>{t("groups.table.name")}</Table.Head.Th>
                    <Table.Head.Th>{t("groups.table.description")}</Table.Head.Th>
                    <Table.Head.Th>{t("groups.table.users")}</Table.Head.Th>
                    <Table.Head.Th className="w-0" />
                </Table.Head.Tr>
            </Table.Head>
            <Table.ConditionalBody show={!isLoading}>
                {model?.groups?.map(group => createGroupRow(group, openConfirmationDialog, editGroup))}
            </Table.ConditionalBody>
            <Table.ConditionalFoot show={!isLoading}>
                <Table.Foot.Pagination
                    className={"fixed bottom-4 left-1/2 -translate-x-1/2"}
                    currentPage={model?.pagination.current ?? 1}
                    pageCount={model?.pagination.pageCount ?? 1}
                    onPageChange={changePage}/>
            </Table.ConditionalFoot>
        </Table>
    </>;
}

function createGroupRow(group: Group, onDelete: (_: string) => void, onEdit: (_: string) => void) {
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
                onClick={() => onEdit(group.name)}
                title={t("groups.table.actions.edit")}/>
            <DeleteButton aria-label={t("groups.table.actions.deleteAria")} disabled={group.isSystemGroup}
                title={t("groups.table.actions.delete")}
                onClick={() => onDelete(group.name)}/>
        </Table.Body.Td>
    </Table.Body.Tr>;
}