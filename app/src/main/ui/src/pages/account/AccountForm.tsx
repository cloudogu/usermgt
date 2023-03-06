import {Button, Form, useFormHandler} from "@cloudogu/ces-theme-tailwind";
import {saveAccount} from "../../hooks/useAccount";
import {t} from "../../helpers/i18nHelpers";
import {useChangeNotification} from "../../hooks/useChangeNotification";
import {ApiAccount} from "../../services/Account";

type AccountFormProps = {
    account: ApiAccount;
    validationSchema: any;
    setAccount: (account: ApiAccount) => void;
}

export default function AccountForm(props: AccountFormProps) {
    const [notification, successAlert, errorAlert] = useChangeNotification();

    const handler = useFormHandler<any>({
        initialValues: {
            ...props.account,
            confirmPassword: props.account.password,
            hiddenPasswordField: props.account.password,
        },
        validationSchema: props.validationSchema,
        enableReinitialize: true,
        onSubmit: (values: any) => {
            saveAccount(values)
                .catch((error: Error) => {
                    errorAlert(error.message);
                })
                .then(value => {
                    if (value !== undefined) {
                        successAlert(value);
                    }
                    props.setAccount(values);
                    handler.resetForm(values);
                });
        },
    });

    return <Form handler={handler}>
        {notification}
        <Form.ValidatedTextInput type={"text"} name={"username"}
                                 disabled={true}>{t('editUser.labels.username')}</Form.ValidatedTextInput>
        <Form.ValidatedTextInput type={"text"} name={"givenname"}
        >{t('editUser.labels.givenName')}</Form.ValidatedTextInput>
        <Form.ValidatedTextInput type={"text"} name={"surname"}
        >{t('editUser.labels.surname')}</Form.ValidatedTextInput>
        <Form.ValidatedTextInput type={"text"} name={"displayName"}
        >{t('editUser.labels.displayName')}</Form.ValidatedTextInput>
        <Form.ValidatedTextInput type={"text"} name={"mail"}
        >{t('editUser.labels.email')}</Form.ValidatedTextInput>
        <Form.ValidatedTextInput type={"password"} name={"password"}
        >{t('editUser.labels.password')}</Form.ValidatedTextInput>
        <Form.ValidatedTextInput type={"password"} name={"confirmPassword"}
        >{t('editUser.labels.confirmPassword')}</Form.ValidatedTextInput>
        <div className={"my-4"}>
            <Button variant={"primary"} type={"submit"}
                    disabled={!handler.dirty}>{t('editUser.buttons.save')}</Button>
        </div>
    </Form>
}
