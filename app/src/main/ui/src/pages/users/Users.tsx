import {useEffect} from "react";
import {Table} from "@cloudogu/ces-theme-tailwind";
import {User, useUsers} from "../../hooks/useUsers";
import {TrashIcon, PencilIcon} from "@heroicons/react/24/outline";

export default function Users(props: {title: string}) {
  const users: User[]  = useUsers()
  useEffect(() => {(document.title = props.title)}, []);
  return <>
    <h1 className="text-heading-font text-4xl mt-5 mb-2.5 uppercase">Benutzer</h1>
    <Table className="mt-4 text-sm">
      <Table.Head>
        <Table.Head.Tr className={"uppercase"}>
          <Table.Head.Th>Benutzername</Table.Head.Th>
          <Table.Head.Th>Anzeigename</Table.Head.Th>
          <Table.Head.Th>E-Mail</Table.Head.Th>
          <Table.Head.Th className="w-0">Aktionen</Table.Head.Th>
        </Table.Head.Tr>
      </Table.Head>
      <Table.Body>
        {users.map(user => {
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
              <button className={"text-text-primary hover:text-text-primary-hover"}>
                <PencilIcon className={"w-6 h-6"}/>
              </button>
              <button className={"text-text-primary hover:text-text-primary-hover"}>
                <TrashIcon className={"w-6 h-6"}/>
              </button>
            </Table.Body.Td>
          </Table.Body.Tr>
        })}
      </Table.Body>
    </Table>
  </>;
}
