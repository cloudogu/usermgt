import {AccountService, initialState} from "../services/Account";
import {useAPI} from "./useAPI";
import type {ApiAccount} from "../services/Account";

export function useAccount() {
    const {data, isLoading, setData:setAccount} = useAPI<ApiAccount>(AccountService.get);
    const account = data ?? initialState;
    return {account, isLoading, setAccount};
}
