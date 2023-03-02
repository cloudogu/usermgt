import {QueryOptions} from "../hooks/useAPI";
import {Axios} from "../api/axios";

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

export const initialState: ApiAccount = {
    displayName: "",
    givenname: "",
    mail: "",
    surname: "",
    username: "",
    password: "",
    pwdReset: false,
    memberOf: []
}

export const AccountService = {
    async get(signal?: AbortSignal, _?: QueryOptions): Promise<ApiAccount> {
        return new Promise<ApiAccount>(async (resolve, reject) => {
            try {
                const accountResponse = await Axios.get<ApiAccount>("/account", {
                    signal: signal
                });
                if (!accountResponse.data) {
                    reject(new Error("failed to load group data: " + accountResponse.status));
                }
                const accountData = accountResponse.data;
                resolve(accountData);
            } catch (e) {
                reject(e);
            }
        })
    },
}