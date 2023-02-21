import {useEffect} from "react";
import {Table} from "@cloudogu/ces-theme-tailwind";

export default function Users(props: {title: string}) {
  useEffect(() => {(document.title = props.title)}, []);
  return <>
    <h1 className="bg-heading-font text-4xl">Benutzer</h1>
    <Table>
      <Table.Head>
        <Table.Head.Tr>
          <Table.Head.Th>Benutzername</Table.Head.Th>
          <Table.Head.Th>Anzeigename</Table.Head.Th>
          <Table.Head.Th>E-Mail</Table.Head.Th>
          <Table.Head.Th>Aktionen</Table.Head.Th>
        </Table.Head.Tr>
      </Table.Head>
      <Table.Body>
        <Table.Body.Tr>
          <Table.Body.Td></Table.Body.Td>
        </Table.Body.Tr>
      </Table.Body>
    </Table>
  </>;
}
