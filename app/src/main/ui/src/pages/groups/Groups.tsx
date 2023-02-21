import {useEffect} from "react";
import {Table} from "@cloudogu/ces-theme-tailwind";
import {Group, useGroups} from "../../hooks/useGroups";

export default function Groups(props: {title: string}) {
  const groups: Group[]  = useGroups()
  useEffect(() => {(document.title = props.title)}, [])
  return <>
    <h1 className="text-heading-font text-4xl mt-5 mb-2.5">Gruppen</h1>
    <Table className="mt-4">
      <Table.Head>
        <Table.Head.Tr>
          <Table.Head.Th>Name</Table.Head.Th>
          <Table.Head.Th>Beschreibung</Table.Head.Th>
          <Table.Head.Th>Benutzer</Table.Head.Th>
          <Table.Head.Th>Aktionen</Table.Head.Th>
        </Table.Head.Tr>
      </Table.Head>
      <Table.Body>
        {groups.map(group => {
          return <Table.Body.Tr>
            <Table.Body.Td>
              <span className="font-bold">{group.name}</span>
              </Table.Body.Td>
            <Table.Body.Td>{group.description}</Table.Body.Td>
            <Table.Body.Td>{group.members}</Table.Body.Td>
            <Table.Body.Td>löschen</Table.Body.Td>
          </Table.Body.Tr>
        })}
      </Table.Body>
    </Table>
  </>;
}
