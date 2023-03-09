import {useState} from "react";
import {calcPageStart, createPaginationData} from "../lib/pagination";

const PAGE_SIZE=5;

export function usePagination(entryCount: number) {
    const [currentPage, setCurrentPage] = useState<number>(1);
    const pageStart = calcPageStart(currentPage, PAGE_SIZE);

    const pgData = createPaginationData(pageStart, PAGE_SIZE, entryCount);

    return {pageStart, setCurrentPage, pgData};
}