import React from "react";
import {Button, H1, LoadingIcon, Searchbar, Table} from "@cloudogu/ces-theme-tailwind";
import {useGroups} from "../../hooks/useGroups";
import {TrashIcon, PencilIcon} from "@heroicons/react/24/outline";
import {t} from "../../helpers/i18nHelpers";
import {Group, GroupsModel} from "../../api/GroupsAPI";
import {ChevronDoubleLeftIcon, ChevronDoubleRightIcon} from "@heroicons/react/24/outline";
import {useFilter} from "../../hooks/useFilter";
import {useSetPageTitle} from "../../hooks/useSetPageTitle";

export default function Groups(props: { title: string }) {
    useSetPageTitle(props.title)
    const [setQuery, setPage, opts] = useFilter();
    const changePage = (selectedPage: number) => {
        setPage(selectedPage)
    };
    const onSearch = (query: string) => {
        setQuery(query);
    };
    const [groups, isLoading] = useGroups(opts);

    return <>
        <div className="flex justify-between">
            <H1 className="uppercase">{t("pages.groups")}</H1>
            <div className="flex justify-between py-1">
                <Button variant={"secondary"} className="mt-5 mb-2.5 mr-5"
                        disabled={isLoading}>{t("groups.create")}</Button>
                <form onSubmit={(e) => e.preventDefault()}>
                    <Searchbar placeholder={"Filter"} clearOnSearch={false} onSearch={onSearch}
                               className="mt-5 mb-2.5"/>
                </form>
            </div>
        </div>
        {isLoading ?
            <div className={"flex justify-center w-[100%] mt-4"}>
                <LoadingIcon className={"w-64 h-64"}/>
            </div>
            :
            <Table className="mt-4 text-sm">
                <Table.Head>
                    <Table.Head.Tr className={"uppercase"}>
                        <Table.Head.Th>{t("groups.table.name")}</Table.Head.Th>
                        <Table.Head.Th>{t("groups.table.description")}</Table.Head.Th>
                        <Table.Head.Th>{t("groups.table.users")}</Table.Head.Th>
                        <Table.Head.Th className="w-0"></Table.Head.Th>
                    </Table.Head.Tr>
                </Table.Head>
                <Table.Body>
                    {groups?.groups?.map(createGroupRow)}
                </Table.Body>
            </Table>}
        {renderPager(changePage, groups)}
    </>;
}

function renderPager(changePage: (page: number) => void, model?: GroupsModel) {
    const currentPage = model?.currentPage ?? 1;
    const maxPages = model?.maxPages ?? 1;
    return <div className="flex justify-between">
        <Button variant={"secondary"} disabled={currentPage === 1}
                onClick={() => changePage(currentPage - 1)}>
            <ChevronDoubleLeftIcon className="w-3 h-3"/>
        </Button>
        {[...Array(model?.maxPages)].map((x, i) =>
            <Button variant={currentPage === (i + 1) ? "primary" : "secondary"} key={i}
                    onClick={() => changePage(i + 1)}>{i + 1}</Button>
        )}
        <form>
            <Button variant={"secondary"} disabled={maxPages === currentPage}
                    onClick={() => changePage(currentPage + 1)}>
                <ChevronDoubleRightIcon className="w-3 h-3"/>
            </Button>
        </form>
    </div>;
}

function createGroupRow(group: Group) {
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
            <button aria-label={t("groups.table.actions.editAria")}
                    className={"text-text-primary hover:text-text-primary-hover mr-2.5"}
                    title={t("groups.table.actions.edit")}>
                <PencilIcon className={"w-6 h-6"}/>
            </button>
            <button aria-label={t("groups.table.actions.deleteAria")} disabled={group.isSystemGroup}
                    className={"enabled:text-text-primary enabled:hover:text-text-primary-hover text-text-primary-disabled disabled:cursor-not-allowed"}
                    title={t("groups.table.actions.delete")}>
                <TrashIcon className={"w-6 h-6"}/>
            </button>
        </Table.Body.Td>
    </Table.Body.Tr>
}