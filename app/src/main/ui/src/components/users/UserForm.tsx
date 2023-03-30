import {Button, Form} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {t} from "../../helpers/i18nHelpers";
import useUserFormHandler from "../../hooks/useUserFormHandler";
import type {User} from "../../services/Users";
import type { NotifyFunction, UseFormHandlerFunctions} from "@cloudogu/ces-theme-tailwind";

// eslint-disable-next-line autofix/no-unused-vars
export type OnSubmitUserForm<T extends User> = (values: T, notify: NotifyFunction, handler: UseFormHandlerFunctions<T>) => Promise<void> | void;

export interface UserFormProps<T extends User> {
    initialUser: T;
    children?: JSX.Element;
    additionalButtons?: JSX.Element;
    disableUsernameField?: boolean;
    onSubmit: OnSubmitUserForm<T>;
    backButton?: boolean;
}

export default function UserForm<T extends User>(props: UserFormProps<T>) {
    const {handler, notification, notify} = useUserFormHandler<T>(props.initialUser, (values: T) => props.onSubmit(values, notify, handler));

    return <Form handler={handler}>
        {notification}
        <Form.ValidatedTextInput type={"text"} name={"username"} disabled={props.disableUsernameField ?? true}>
            {t("editUser.labels.username")}
        </Form.ValidatedTextInput>
        <Form.ValidatedTextInput type={"text"} name={"givenname"}>
            {t("editUser.labels.givenName")}
        </Form.ValidatedTextInput>
        <Form.ValidatedTextInput type={"text"} name={"surname"}>
            {t("editUser.labels.surname")}
        </Form.ValidatedTextInput>
        <Form.ValidatedTextInput type={"text"} name={"displayName"}>
            {t("editUser.labels.displayName")}
        </Form.ValidatedTextInput>
        <Form.ValidatedTextInput type={"text"} name={"mail"}>
            {t("editUser.labels.email")}
        </Form.ValidatedTextInput>
        <Form.ValidatedTextInput type={"password"} name={"password"}>
            {t("editUser.labels.password")}
        </Form.ValidatedTextInput>
        <Form.ValidatedTextInput type={"password"} name={"confirmPassword"}>
            {t("editUser.labels.confirmPassword")}
        </Form.ValidatedTextInput>
        {props.children as JSX.Element}
        <div className={"my-4"}>
            <Button variant={"primary"} type={"submit"} disabled={!handler.dirty}>
                {t("editUser.buttons.save")}
            </Button>
            {props.additionalButtons as JSX.Element}
        </div>
    </Form>;
}
