import React, {useEffect} from "react";
import {H1, LoadingIcon, Table} from "@cloudogu/ces-theme-tailwind";
import {useGroups} from "../../hooks/useGroups";
import {TrashIcon, PencilIcon} from "@heroicons/react/24/outline";
import {t} from "../../helpers/i18nHelpers";

export default function Groups(props: { title: string }) {
    const [groups, isLoading] = useGroups()

    useEffect(() => {
        (document.title = props.title)
    }, [])

    if (isLoading) {
        return (
            <div className={"flex row justify-center w-[100%] mt-16"}>
                <LoadingIcon className={"w-64 h-64"}/>
            </div>
        )
    }

    return <>
        <H1 className="uppercase">{t("pages.groups")}</H1>
        <Table className="mt-4 text-sm">
            <Table.Head>
                <Table.Head.Tr className={"uppercase"}>
                    <Table.Head.Th>{t("groups.name")}</Table.Head.Th>
                    <Table.Head.Th>{t("groups.description")}</Table.Head.Th>
                    <Table.Head.Th>{t("groups.users")}</Table.Head.Th>
                    <Table.Head.Th className="w-0"></Table.Head.Th>
                </Table.Head.Tr>
            </Table.Head>
            <Table.Body>
                {groups.map(group => {
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
                })}
            </Table.Body>
        </Table>
    </>;
}
