import {Button, Form, useFormHandler} from "@cloudogu/ces-theme-tailwind";
import {t} from "../../helpers/i18nHelpers";
import {useChangeNotification} from "../../hooks/useChangeNotification";
import {AccountService} from "../../services/Account";
import type {ApiAccount, AccountModel} from "../../services/Account";
import useUserFormHandler from "../../hooks/useUserFormHandler";
import {User} from "../../services/Users";

export interface UserFormProps<T extends User> {
    initialUser: T;
    onUserChange: (_user: T) => void;
    saveUser: (_user: T) => Promise<string>;
    children?: JSX.Element;
}

export default function UserForm<T extends User>(props: UserFormProps<T>) {
    const {handler, notification} = useUserFormHandler(props.initialUser, props.saveUser, props.onUserChange);

    return <Form handler={handler}>
        {notification}
        <Form.ValidatedTextInput type={"text"} name={"username"} disabled={true}>
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
        </div>
    </Form>;
}
