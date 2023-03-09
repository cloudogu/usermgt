import {SearchbarAutocomplete, Table} from "@cloudogu/ces-theme-tailwind";
import React, {useState} from "react";
import {DeleteButton} from "../../components/DeleteButton";
import {t} from "../../helpers/i18nHelpers";
import {usePagination} from "../../hooks/usePagination";
import type {QueryOptions} from "../../hooks/useAPI";

const PAGE_SIZE=5;

export type MembersProps = {
    entries: string[];
    addEntry: (_:string) => void;
    removeEntry: (_:string) => void;
    loadFn: (_:QueryOptions) => Promise<string[]>;
}

export function Members(props: MembersProps) {
    const [entries, setEntries] = useState<string[]>([]);
    const {pgData, pageStart, setCurrentPage} = usePagination(entries.length);
    return <>
        <SearchbarAutocomplete
            searchResults={entries.map(x => <SearchbarAutocomplete.SearchResult type={"button"} key={x} value={x} />)}
            onSelectItem={(val: string, item) => {
                props.addEntry(val);
                item.value = "";
                item.focus();
                setEntries([]);
            }}
            onTrigger={async (val) => {
                const newEntries = await props.loadFn({start: 0, limit: PAGE_SIZE, query: val});
                const shouldUpdateResultList = containsNewEntries(entries, newEntries);
                if (shouldUpdateResultList){
                    setEntries(newEntries);
                }
            }}
            onCancelSelection={(item) => {
                item.value = "";
                setEntries([]);
            }}>
        Mitglied hinzufügen
        </SearchbarAutocomplete>
        <Table className="my-4 text-sm">
            <Table.Head>
                <Table.Head.Tr className={"uppercase"}>
                    <Table.Head.Th>{t("users.table.username")}</Table.Head.Th>
                    <Table.Head.Th className="w-0"/>
                </Table.Head.Tr>
            </Table.Head>
            <Table.Body>
                {showPagedEntries([...props.entries], pageStart, PAGE_SIZE).map((user) => <Table.Body.Tr key={user}>
                    <Table.Body.Td className="font-bold">{user}</Table.Body.Td>
                    <Table.Body.Td className="flex justify-center">
                        <DeleteButton
                            type={"button"}
                            title={t("users.table.actions.delete")}
                            onClick={() => props.removeEntry(user)}/>
                    </Table.Body.Td>
                </Table.Body.Tr>)}
            </Table.Body>
            <Table.ConditionalFoot show={true}>
                <Table.Foot.Pagination pageCount={pgData.pageCount} currentPage={pgData.pageCount} onPageChange={(page) => setCurrentPage(page)} />
            </Table.ConditionalFoot>
        </Table>
    </>;
}

const showPagedEntries = (entries: string[], start: number, limit: number) => {
    const sortedEntries = entries.sort(compareString);
    if (start >= sortedEntries.length){
        return sortedEntries;
    }
    return sortedEntries.slice(start, start + limit);
};

const compareString = (a: string, b: string): number => a.toLowerCase() > b.toLowerCase() ? 1 : -1;

const containsNewEntries = (oldList: string[], newList: string[]) => {
    if (oldList.length < newList.length) {
        return true;
    }
    for (const newItem in newList){
        if(oldList.indexOf(newItem) >= 0){
            return true;
        }
    }
    return false;
};