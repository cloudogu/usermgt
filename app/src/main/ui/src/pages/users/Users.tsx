import React from "react";
import {Button, H1, LoadingIcon, Searchbar, Table} from "@cloudogu/ces-theme-tailwind";
import {useUsers} from "../../hooks/useUsers";
import {TrashIcon, PencilIcon} from "@heroicons/react/24/outline";
import {t} from "../../helpers/i18nHelpers";
import {User} from "../../api/UsersAPI";
import {useSetPageTitle} from "../../hooks/useSetPageTitle";
import {useFilter} from "../../hooks/useFilter";

export default function Users(props: { title: string }) {
    useSetPageTitle(props.title)
    const [setQuery, _, opts] = useFilter();
    const onSearch = (query: string) => {
        setQuery(query);
    }
    const [users, isLoading] = useUsers(opts)

    return <>
        <div className="flex justify-between">
            <H1 className="uppercase">{t("pages.users")}</H1>
            <div className="flex justify-between py-1">
                <Button variant={"secondary"} className="mt-5 mb-2.5 mr-5" disabled={isLoading}>{t("users.create")}</Button>
                <Searchbar placeholder={"Filter"} clearOnSearch={false} onSearch={onSearch} className="mt-5 mb-2.5"/>
            </div>
        </div>
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
                    {users.map(createUsersRow)}
                </Table.Body>
            </Table>)}
    </>;
}

function createUsersRow(user: User) {
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
            <button className={"text-text-primary hover:text-text-primary-hover mr-2.5"}>
                <PencilIcon className={"w-6 h-6"}/>
            </button>
            <button className={"text-text-primary hover:text-text-primary-hover"}>
                <TrashIcon className={"w-6 h-6"}/>
            </button>
        </Table.Body.Td>
    </Table.Body.Tr>;
}