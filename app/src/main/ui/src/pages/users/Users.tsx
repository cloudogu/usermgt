import {useEffect} from "react";
import {Table} from "@cloudogu/ces-theme-tailwind";
import {User, useUsers} from "../../hooks/useUsers";

export default function Users(props: {title: string}) {
  const users: User[]  = useUsers()
  useEffect(() => {(document.title = props.title)}, []);
  return <>
    <h1 className="text-heading-font text-4xl mt-5 mb-2.5">Benutzer</h1>
    <Table className="mt-4">
      <Table.Head>
        <Table.Head.Tr>
          <Table.Head.Th>Benutzername</Table.Head.Th>
          <Table.Head.Th>Anzeigename</Table.Head.Th>
          <Table.Head.Th>E-Mail</Table.Head.Th>
          <Table.Head.Th>Aktionen</Table.Head.Th>
        </Table.Head.Tr>
      </Table.Head>
      <Table.Body>
        {users.map(user => {
          return <Table.Body.Tr>
            <Table.Body.Td>
              <span className="font-bold">{user.username}</span>
            </Table.Body.Td>
            <Table.Body.Td>{user.displayName}</Table.Body.Td>
            <Table.Body.Td>{user.mail}</Table.Body.Td>
            <Table.Body.Td>löschen</Table.Body.Td>
          </Table.Body.Tr>
        })}
      </Table.Body>
    </Table>
  </>;
}
