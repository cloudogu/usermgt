import {AccountService, emptyUser} from "../services/Account";
import {useAPI} from "./useAPI";
import type {ApiAccount} from "../services/Account";

export function useAccount() {
    const {data, isLoading, setData:setAccount} = useAPI<ApiAccount>(AccountService.get);
    const account = data ?? emptyUser;
    return {account, isLoading, setAccount};
}
