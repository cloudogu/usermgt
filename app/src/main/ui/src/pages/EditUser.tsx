import {Button, H1, LoadingIcon} from "@cloudogu/deprecated-ces-theme-tailwind";
import React, {useContext} from "react";
import {useNavigate, useParams} from "react-router-dom";
import {ApplicationContext} from "../components/contexts/ApplicationContext";
import UserForm from "../components/users/UserForm";
import {t} from "../helpers/i18nHelpers";
import {useBackURL} from "../hooks/useBackURL";
import {useUser} from "../hooks/useUser";
import {isUsersConstraintsError, UserConstraints, type UsersConstraintsError, UsersService} from "../services/Users";
import type {User} from "../services/Users";

export default function EditUser() {
    const {casUser} = useContext(ApplicationContext);
    const {username} = useParams();
    const {user, isLoading} = useUser(username);
    const navigate = useNavigate();
    const {backURL} = useBackURL();

    return <>
        <H1 className="uppercase">{t("pages.usersEdit")}</H1>
        {casUser.admin && (isLoading ?
            <div className={"flex row justify-center w-[100%] mt-16"}>
                <LoadingIcon className={"w-64 h-64"}/>
            </div>
            :
            <UserForm<User>
                initialUser={user}
                groupsReadonly={false}
                passwordReset={casUser.principal !== username}
                onSubmit={(user, notify, handler) =>
                    UsersService.update(user)
                        .then((msg: string) => {
                            navigate(backURL ?? "/users", {
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

                                if (error.constraints.includes(UserConstraints.ValidEmail)) {
                                    const msg = t("newUser.notification.errorInvalidMail");
                                    messages.push(msg);
                                    handler.setFieldError("mail", msg);
                                }


                                notify((<>{messages.map((msg, i) => <div key={i}>{msg}</div>)}</>), "danger");
                            } else {
                                notify(error.message, "danger");
                            }
                        })}

                additionalButtons={
                    <Button variant={"secondary"} type={"button"} className={"ml-4"}
                        data-testid="back-button"
                        onClick={() => navigate(backURL ?? "/users")}>
                        {t("editGroup.buttons.back")}
                    </Button>
                }
            />)
        }
    </>;
}
