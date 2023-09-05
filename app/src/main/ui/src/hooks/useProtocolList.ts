import {ImportUsersService} from "../services/ImportUsers";
import {useAPI} from "./useAPI";
import type {QueryOptions} from "./useAPI";
import type { SummariesModel} from "../services/ImportUsers";

export default function useProtocolList(opts: QueryOptions) {
    return useAPI<SummariesModel, QueryOptions>(ImportUsersService.listSummaries, opts);
}