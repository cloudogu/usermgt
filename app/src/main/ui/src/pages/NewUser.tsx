import {Button, H1} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {useNavigate} from "react-router-dom";
import UserForm from "../components/users/UserForm";
import {t} from "../helpers/i18nHelpers";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import {emptyUser} from "../services/Account";
import {isUsersConstraintsError, UserConstraints, UsersConstraintsError, UsersService} from "../services/Users";
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
            onSubmit={(user, notify, handler) => {
                return UsersService.save(user)
                    .then((msg: string) => {
                        navigate("/users", {
                            state: {
                                alert: {
                                    message: msg,
                                    variant: "primary"
                                }
                            }
                        });
                    }).catch((error: UsersConstraintsError | Error) => {
                        if (isUsersConstraintsError(error)) {
                            const messages = [];
                            if (error.constraints.includes(UserConstraints.UniqueUsername)) {
                                const msg = t("newUser.notification.errorDuplicateUsername", {username: user.username});
                                messages.push(msg);
                                handler.setFieldError("username", msg);
                            }

                            if (error.constraints.includes(UserConstraints.UniqueEmail)) {
                                const msg = t("newUser.notification.errorDuplicateMail", {mail: user.mail});
                                messages.push(msg);
                                handler.setFieldError("mail", msg);
                            }

                            notify((<>{ messages.map((msg, i) => <div key={i}>{msg}</div>)}</>), "danger");
                        } else {
                            notify(error.message, "danger");
                        }
                    });
            }}
            additionalButtons={
                <Button variant={"secondary"} type={"button"} className={"ml-4"} data-testid="back-button"
                    onClick={() => navigate("/users")}>
                    {t("editGroup.buttons.back")}
                </Button>
            }
        />
    </>;
}

