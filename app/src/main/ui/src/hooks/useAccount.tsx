import {useEffect, useState} from "react";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

export type ApiAccount = {
    displayName: string,
    givenName: string,
    mail: string,
    surname: string,
    username: string,
    password: string,
    pwdReset: boolean,
    memberOf: string[];
}

const initialState: ApiAccount = {
    displayName: "",
    givenName: "",
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

    return {account, isLoading};
}
