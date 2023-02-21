import {useEffect, useState} from "react";
import i18n from 'i18next';


const contextPath = process.env.PUBLIC_URL || "/usermgt";

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
        fetch(contextPath + `/api/account`)
            .then(async function (response) {
                const json: ApiAccount = await response.json();
                setAccount(json);
                setIsLoading(false);
            });
    }, []);

    return {account, isLoading, setAccount};
}

export function saveAccount(account: ApiAccount) {
    return fetch(contextPath + `/api/account`, {
        method: 'PUT',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(account, ['displayName', 'password', 'username', 'surname', 'mail', 'givenname', 'memberOf', 'pwdReset']),
    }).then(async function (response) {
        if (!response.ok) {
            throw new Error(i18n.t('editUser.alerts.error') as string)
        }
        return i18n.t('editUser.alerts.success') as string
    });
}