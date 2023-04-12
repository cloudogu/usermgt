import {Button, H1} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {useNavigate} from "react-router-dom";
import UserForm from "../components/users/UserForm";
import {t} from "../helpers/i18nHelpers";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import {emptyUser} from "../services/Account";
import { UsersService} from "../services/Users";
import type {User} from "../services/Users";

export default function NewUser(props: { title: string }) {
    useSetPageTitle(props.title);
    const navigate = useNavigate();

    return <>
        <H1 className="uppercase">{t("pages.usersNew")}</H1>
        <UserForm<User>
            initialUser={emptyUser}
            groupsReadonly={false}
            passwordReset={true}
            disableUsernameField={false}
            onSubmit={(user, notify) => UsersService.save(user)
                .then((msg: string) => {
                    navigate("/users", {
                        state: {
                            alert: {
                                message: msg,
                                variant: "primary"
                            }
                        }
                    });
                }).catch((error: Error) => {
                    notify(error.message, "danger");
                })}
            additionalButtons={
                <Button variant={"secondary"} type={"button"} className={"ml-4"}
                    onClick={() => navigate("/users")}>
                    {t("editGroup.buttons.back")}
                </Button>
            }
        />
    </>;
}

