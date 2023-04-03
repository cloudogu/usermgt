import {SearchbarAutocomplete, Table} from "@cloudogu/ces-theme-tailwind";
import React, {useState} from "react";
import {DeleteButton} from "../DeleteButton";
import {t} from "../../helpers/i18nHelpers";
import {usePagination} from "../../hooks/usePagination";
import type {QueryOptions} from "../../hooks/useAPI";

const PAGE_SIZE = 5;

export type ListWithSearchbarProps = {
    entries: string[];
    setEntries: (_: string[]) => void;
    loadSearchResults: (_: QueryOptions) => Promise<string[]>;
    enableDelete?: boolean;
    tableTitle: string;
}

export function ListWithSearchbar(props: ListWithSearchbarProps) {
    const [searchResults, setSearchResults] = useState<string[]>([]);
    const {pgData, pageStart, setCurrentPage} = usePagination(props.entries.length);
    const enableDelete = props.enableDelete ?? true;

    return <>
        <SearchbarAutocomplete
            searchResults={searchResults.map((x, i) => <SearchbarAutocomplete.SearchResult<string> type={"button"} key={i} value={x} />)}
            onSelectItem={(val: string, item) => {
                props.setEntries([...searchResults, val])
                item.value = "";
                item.focus();
                setSearchResults([]);
            }}
            onTrigger={async (val) => {
                const newEntries = await props.loadSearchResults({start: 0, limit: PAGE_SIZE, query: val});
                const shouldUpdateResultList = containsNewEntries(searchResults, newEntries);
                if (shouldUpdateResultList) {
                    setSearchResults(newEntries);
                }
            }}
            onCancelSelection={(item) => {
                item.value = "";
                setSearchResults([]);
            }}>
            {t("generic.labels.addEntry")}
        </SearchbarAutocomplete>
        <Table className="my-4 text-sm">
            <Table.Head>
                <Table.Head.Tr className={"uppercase"}>
                    <Table.Head.Th>{props.tableTitle}</Table.Head.Th>
                    {enableDelete && <Table.Head.Th className="w-0"/>}
                </Table.Head.Tr>
            </Table.Head>
            <Table.Body>
                {showPagedEntries([...props.entries], pageStart, PAGE_SIZE).map((entry) => <Table.Body.Tr key={entry}>
                    <Table.Body.Td className="font-bold">{entry}</Table.Body.Td>
                    {enableDelete &&
                        <Table.Body.Td className="flex justify-center">
                            <DeleteButton
                                type={"button"}
                                title={t("generic.table.actions.delete")}
                                onClick={() => props.setEntries([...removeEntry(searchResults, entry)])}/>
                        </Table.Body.Td>
                    }
                </Table.Body.Tr>)}
            </Table.Body>
            <Table.ConditionalFoot show={true}>
                <Table.Foot.Pagination pageCount={pgData.pageCount} currentPage={pgData.pageCount} onPageChange={(page) => setCurrentPage(page)}/>
            </Table.ConditionalFoot>
        </Table>
    </>;
}

const showPagedEntries = (entries: string[], start: number, limit: number) => {
    const sortedEntries = entries.sort(compareString);
    if (start >= sortedEntries.length) {
        return sortedEntries;
    }
    return sortedEntries.slice(start, start + limit);
};

const compareString = (a: string, b: string): number => a.toLowerCase() > b.toLowerCase() ? 1 : -1;

const containsNewEntries = (oldList: string[], newList: string[]) => {
    if (oldList.length < newList.length) {
        return true;
    }
    for (const newItem in newList) {
        if (oldList.indexOf(newItem) >= 0) {
            return true;
        }
    }
    return false;
};

function removeEntry(members: string[], toRemove: string): string[] {
    const index = members.indexOf(toRemove);
    if (index < 0) {
        return members;
    }
    members.splice(index, 1);
    return members;
}
