import {useEffect} from "react";
import {Table} from "@cloudogu/ces-theme-tailwind";
import {Group, useGroups} from "../../hooks/useGroups";
import {TrashIcon, PencilIcon} from "@heroicons/react/24/outline";
import {UndeletableGroups, useUndeletableGroups} from "../../hooks/useUndeletableGroups";

export default function Groups(props: { title: string }) {
    const groups: Group[] = useGroups()
    const undeletableGroups: UndeletableGroups = useUndeletableGroups()

    useEffect(() => {
        (document.title = props.title)
    }, [])
    return <>
        <h1 className="text-heading-font text-4xl mt-5 mb-2.5 uppercase">Gruppen</h1>
        <Table className="mt-4 text-sm">
            <Table.Head>
                <Table.Head.Tr className={"uppercase"}>
                    <Table.Head.Th>Name</Table.Head.Th>
                    <Table.Head.Th>Beschreibung</Table.Head.Th>
                    <Table.Head.Th>Benutzer</Table.Head.Th>
                    <Table.Head.Th className="w-0">Aktionen</Table.Head.Th>
                </Table.Head.Tr>
            </Table.Head>
            <Table.Body>
                {groups.map(group => {
                    let isSystemGroup: boolean = undeletableGroups.includes(group.name);
                    return <Table.Body.Tr key={group.name}>
                        <Table.Body.Td>
                            <span className="font-bold">{group.name}</span>
                        </Table.Body.Td>
                        <Table.Body.Td>
                            <p>{group.description}</p>
                            {isSystemGroup ?
                                <p className="font-bold">This is a system group. It cannot be deleted.</p> : ""}
                        </Table.Body.Td>
                        <Table.Body.Td>{group.members?.length ?? 0}</Table.Body.Td>
                        <Table.Body.Td className="flex justify-center">
                            <button className={"text-text-primary hover:text-text-primary-hover"}>
                                <PencilIcon className={"w-6 h-6"}/>
                            </button>
                            <button aria-label="entfernen" disabled={isSystemGroup}
                                    className={"enabled:text-text-primary enabled:hover:text-text-primary-hover text-text-primary-disabled disabled:cursor-not-allowed"}
                                    title="Gruppe entfernen">
                                <TrashIcon className={"w-6 h-6"}/>
                            </button>
                        </Table.Body.Td>
                    </Table.Body.Tr>
                })}
            </Table.Body>
        </Table>
    </>;
}
