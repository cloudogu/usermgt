import { isAxiosError} from "axios";
import {Axios} from "../api/axios";
import {t} from "../helpers/i18nHelpers";
import type {User, UsersConstraintsError} from "./Users";
import type {AxiosError} from "axios";


export type ApiAccount = User;

export type AccountModel = ApiAccount & {
    hiddenPasswordField?: string;
    confirmPassword?: string;
}

export const emptyUser: User = {
    displayName: "",
    givenname: "",
    mail: "",
    surname: "",
    username: "",
    password: "",
    pwdReset: false,
    external: false,
    memberOf: []
};

export const AccountService = {
    async get(signal?: AbortSignal): Promise<AccountModel> {
        const accountResponse = await Axios.get<AccountModel>("/account", {
            signal: signal
        });
        if (accountResponse.status < 200 || accountResponse.status > 299) {
            throw new Error("failed to load account data: " + accountResponse.status);
        }
        // set empty string if given name is null
        // formik expects each form value to be at least undefined or empty
        if (accountResponse.data.givenname === null) {
            accountResponse.data.givenname = "";
        }
        return accountResponse.data;
    },
    async update(account: AccountModel): Promise<string> {
        delete account.hiddenPasswordField;
        delete account.confirmPassword;
        try {
            await Axios.put("/account", account, {
                headers: {"Content-Type": "application/json"}
            });
            return t("newUser.notification.success", {username: account.username});
        } catch (e: AxiosError | unknown) {
            if (isAxiosError(e)) {
                const axiosError = e as AxiosError;
                if (axiosError.response?.status === 409) {
                    throw axiosError.response.data as UsersConstraintsError;
                }
            }
            throw new Error(t("editUser.notification.error", {username: account.username}));
        }
    }
};
