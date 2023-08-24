import type {ComponentPropsWithoutRef} from "react";
import {Table} from "@cloudogu/ces-theme-tailwind";

export interface ProtocolListProps extends ComponentPropsWithoutRef<"table">{

}
export default function ProtocolList(props: ProtocolListProps) {
    return (
        <Table {...props}>

        </Table>
    );
}