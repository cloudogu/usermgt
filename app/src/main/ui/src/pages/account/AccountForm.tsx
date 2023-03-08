import {Button, Form, useAlertNotification, useFormHandler} from "@cloudogu/ces-theme-tailwind";
import {t} from "../../helpers/i18nHelpers";
import { AccountService} from "../../services/Account";
import type {ApiAccount,AccountModel} from "../../services/Account";

type AccountFormProps = {
    account: ApiAccount;
    validationSchema: any;
    setAccount: (_: ApiAccount) => void;
}

export default function AccountForm(props: AccountFormProps) {
    const {notification, notify} = useAlertNotification();

    const handler = useFormHandler<AccountModel>({
        initialValues: {
            ...props.account,
            confirmPassword: props.account.password,
            hiddenPasswordField: props.account.password,
        },
        validationSchema: props.validationSchema,
        enableReinitialize: true,
        onSubmit: (values: any) => {
            AccountService.update(values).then(value => {
                notify(value, "primary");
                props.setAccount(values);
                handler.resetForm(values);
            }).catch((error: Error) => {
                notify(error.message, "danger");
            });
        },
    });

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
        <div className={"my-4"}>
            <Button variant={"primary"} type={"submit"} disabled={!handler.dirty}>
                {t("editUser.buttons.save")}
            </Button>
        </div>
    </Form>;
}
