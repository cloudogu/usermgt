import {H1, LoadingIcon} from "@cloudogu/deprecated-ces-theme-tailwind";
import React from "react";
import UserForm from "../components/users/UserForm";
import {t} from "../helpers/i18nHelpers";
import {useAccount} from "../hooks/useAccount";
import {AccountService} from "../services/Account";
import type {User} from "../services/Users";

export default function Account() {
    const {account, isLoading, setAccount} = useAccount();

    return <>
        <H1 className="uppercase">Account</H1>
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
                    }).catch((error: Error) => {
                        const msg = t("newUser.notification.error", {username: account.username});
                        const messages = [];
                        console.log(error.message);
                        messages.push(msg);
                        handler.setFieldError("mail", msg);
                        notify((<>{messages.map((msg, i) => <div key={i}>{msg}</div>)}</>), "danger");
                    })}
            />
        }
    </>;
}

