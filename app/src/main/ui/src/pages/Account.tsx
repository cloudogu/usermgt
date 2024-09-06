import {H1, LoadingIcon} from "@cloudogu/deprecated-ces-theme-tailwind";
import React from "react";
import UserForm from "../components/users/UserForm";
import {t} from "../helpers/i18nHelpers";
import {useAccount} from "../hooks/useAccount";
import {AccountService} from "../services/Account";
import {isUsersConstraintsError, UserConstraints, type User, type UsersConstraintsError, } from "../services/Users";

export default function Account() {
    const {account, isLoading, setAccount} = useAccount();

    return <>
        <H1 className="uppercase">{t("pages.account")}</H1>
        {isLoading ?
            <div className={"flex row justify-center w-[100%] mt-16"}>
                <LoadingIcon className={"w-64 h-64"}/>
            </div>
            :
            <UserForm<User>
                initialUser={account}
                groupsReadonly={true}
                onSubmit={(account, notify, handler) => AccountService.update(account)
                    .then((msg: string) => {
                        notify(msg, "primary");
                        setAccount(account);
                        handler.resetForm({values: account});
                    }).catch((error: UsersConstraintsError | Error) => {
                        const messages = [];
                        if (isUsersConstraintsError(error)) {
                            if (error.constraints.includes(UserConstraints.UniqueEmail)) {
                                const msg = t("newUser.notification.error", {username: account.username});
                                messages.push(msg);
                                handler.setFieldError("mail", msg);
                            }

                            if (error.constraints.includes(UserConstraints.ValidEmail)) {
                                const msg = t("newUser.notification.error", {username: account.username});
                                messages.push(msg);
                                handler.setFieldError("mail", msg);
                            }

                            notify((<>{messages.map((msg, i) => <div key={i}>{msg}</div>)}</>), "danger");
                        } else {
                            const msg = t("newUser.notification.error", {username: account.username});
                            messages.push(msg);
                            notify((<>{messages.map((msg, i) => <div key={i}>{msg}</div>)}</>), "danger");
                        }
                    })}
            />
        }
    </>;
}

