import {ImportUsersResponse, ImportUsersService} from "../services/ImportUsers";
import {useAPI} from "./useAPI";
import type {QueryOptions} from "./useAPI";
import type {PagedModel} from "@cloudogu/ces-theme-tailwind";
import UsersImportResult from "../pages/UsersImportResult";

export interface ImportProtocol {
    name: string;
    date: Date;
    result: ImportUsersResponse,
}

export interface ImportProtocolDto {
    name: string;
    date: number;
    result: ImportUsersResponse,
}

export type ProtocolsDtoModel = PagedModel & {
    protocols: ImportProtocolDto[];
}

export type ProtocolsModel = PagedModel & {
    protocols: ImportProtocol[];
}
export default function useProtocolList(opts: QueryOptions) {
    const {data, isLoading} = useAPI<ProtocolsDtoModel, QueryOptions>(ImportUsersService.listImportProtocols, opts);
    return {
        data: {
            protocols: (data?.protocols as ImportProtocolDto[] ?? []).map(dto => ({
                name: dto.name,
                date: new Date(dto.date),
                result: dto.result,
            } as ImportProtocol)),
            pagination: data?.pagination,
        } as ProtocolsModel, isLoading: isLoading
    };
}