import {useEffect, useState} from "react";
import {Axios} from "../api/axios";
import {t} from "../helpers/i18nHelpers";

export type ApiAccount = {
    displayName: string,
    givenname: string,
    mail: string,
    surname: string,
    username: string,
    password: string,
    pwdReset: boolean,
    memberOf: string[];
}

const initialState: ApiAccount = {
    displayName: "",
    givenname: "",
    mail: "",
    surname: "",
    username: "",
    password: "",
    pwdReset: false,
    memberOf: []
}

export function useAccount() {
    const [account, setAccount] = useState<ApiAccount>(initialState);
    const [isLoading, setIsLoading] = useState<boolean>(true);

    useEffect(() => {
        Axios<ApiAccount>('/account')
            .then(async function (response) {
                const account = await response.data;
                setAccount(account);
                setIsLoading(false);
            });
    }, []);

    return {account, isLoading, setAccount};
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
