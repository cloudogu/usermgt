import {Button, Form, H1} from "@cloudogu/ces-theme-tailwind";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import UserForm from "../components/users/UserForm";
import {User, UsersService} from "../services/Users";
import {t} from "../helpers/i18nHelpers";
import {AccountService, emptyUser} from "../services/Account";
import React, {useState} from "react";
import {useNavigate} from "react-router-dom";
import {useBackURL} from "../hooks/useBackURL";

export default function NewUser(props: { title: string }) {
    useSetPageTitle(props.title);
    const navigate = useNavigate();
    const {backURL} = useBackURL();

    return <>
        <H1 className="uppercase">{t("pages.usersNew")}</H1>
        <UserForm<User>
            initialUser={emptyUser}
            disableUsernameField={false}
            onSubmit={(user, notify, handler) => {
                return UsersService.save(user)
                    .then((msg: string) => {
                        navigate(backURL ?? "/users", {
                            state: {
                                alert: {
                                    message: msg,
                                    variant: "primary"
                                }
                            }
                        });
                    }).catch((error: Error) => {
                        notify(error.message, "danger");
                    });
            }}
            additionalButtons={
                <Button variant={"secondary"} type={"button"} className={"ml-4"}
                        onClick={() => navigate(backURL ?? "/users")}>
                    {t("editGroup.buttons.back")}
                </Button>
            }
        >
            <Form.ValidatedCheckboxLabelRight name={"pwdReset"}>
                {t("editUser.labels.mustChangePassword")}
            </Form.ValidatedCheckboxLabelRight>
        </UserForm>
    </>;
}

