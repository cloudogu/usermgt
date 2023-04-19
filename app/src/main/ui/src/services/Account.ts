import {Axios} from "../api/axios";
import {t} from "../helpers/i18nHelpers";
import {User} from "./Users";


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
        const saveResponse = await Axios.put("/account", account, {
            headers: {"Content-Type": "application/json"}
        });

        if (saveResponse.status < 200 || saveResponse.status > 299) {
            throw new Error(t("editUser.notification.error"));
        }

        return t("editUser.notification.success");
    }
};