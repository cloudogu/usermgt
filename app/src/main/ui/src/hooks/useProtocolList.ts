import {ImportUsersService} from "../services/ImportUsers";
import {useAPI} from "./useAPI";
import type {QueryOptions} from "./useAPI";
import type { ProtocolsModel} from "../services/ImportUsers";

export default function useProtocolList(opts: QueryOptions) {
    return useAPI<ProtocolsModel, QueryOptions>(ImportUsersService.listImportProtocols, opts);
}