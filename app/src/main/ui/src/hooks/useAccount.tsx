import {Axios} from "../api/axios";
import {t} from "../helpers/i18nHelpers";
import {AccountService, initialState} from "../services/Account";
import {useAPI} from "./useAPI";
import type {StateSetter} from "./useAPI";
import type {ApiAccount} from "../services/Account";

export function useAccount(): [ApiAccount, boolean, StateSetter<ApiAccount>] {
    const [account, isLoading, setAccount] = useAPI<ApiAccount>(AccountService.get);
    return [account ?? initialState, isLoading, setAccount];
}

export async function saveAccount(account: ApiAccount): Promise<string> {
    try {
        await Axios("/account", {
            method: "PUT",
            headers: {"Content-Type": "application/json"},
            data: JSON.stringify(account, ["displayName", "password", "username", "surname", "mail", "givenname", "memberOf", "pwdReset"]),
        });
        return t("editUser.alerts.success");
    } catch {
        throw new Error(t("editUser.alerts.error"));
    }
}
