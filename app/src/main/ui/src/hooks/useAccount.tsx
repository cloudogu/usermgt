import {Axios} from "../api/axios";
import {t} from "../helpers/i18nHelpers";
import {StateSetter, useAPI} from "./useAPI";
import {AccountService, ApiAccount, initialState} from "../services/Account";

export function useAccount(): [ApiAccount, boolean, StateSetter<ApiAccount>] {
    const [account, isLoading, setAccount] = useAPI<ApiAccount>(AccountService.get)
    return [account ?? initialState, isLoading, setAccount];
}

export function saveAccount(account: ApiAccount) {
    return new Promise<string>(async (resolve, reject) => {
        try {
            await Axios('/account', {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                data: JSON.stringify(account, ['displayName', 'password', 'username', 'surname', 'mail', 'givenname', 'memberOf', 'pwdReset']),
            });
            resolve(t('editUser.alerts.success'));
        } catch {
            reject(new Error(t('editUser.alerts.error')));
        }
    })
}
