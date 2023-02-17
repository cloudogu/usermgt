import {Alert, Button, Form, H1, useFormHandler} from "@cloudogu/ces-theme-tailwind";
import {ApiAccount, saveAccount} from "../../hooks/useAccount";
import {useState} from "react";
import i18n from 'i18next';

type AccountFormProps = {
  account: ApiAccount;
  validationSchema: any;
  setAccount: (account: ApiAccount) => void;
}

export default function AccountForm(props: AccountFormProps) {
  const [alert, setAlert] = useState(<></>);

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
        .catch(error => {
          setAlert(
            <Alert
              variant={"danger"}
              onClose={() => {
                setAlert(<></>);
              }}>
              {error}
            </Alert>
          );
        })
        .then(value => {
          if (value !== undefined) {
            setAlert(
              <Alert
                variant={"primary"}
                onClose={() => {
                  setAlert(<></>);
                }}>
                {value}
              </Alert>);
          }

          props.setAccount(values);
          handler.resetForm(values);
        });
    },
  });

  return <Form handler={handler}>
    <H1>Account</H1>
    {alert}
    <Form.ValidatedTextInput type={"text"} name={"username"}
                        disabled={true}>{i18n.t('editUser.labels.username')}</Form.ValidatedTextInput>
    <Form.ValidatedTextInput type={"text"} name={"givenname"}
                        >{i18n.t('editUser.labels.givenName')}</Form.ValidatedTextInput>
    <Form.ValidatedTextInput type={"text"} name={"surname"}
                        >{i18n.t('editUser.labels.surname')}</Form.ValidatedTextInput>
    <Form.ValidatedTextInput type={"text"} name={"displayName"}
                        >{i18n.t('editUser.labels.displayName')}</Form.ValidatedTextInput>
    <Form.ValidatedTextInput type={"text"} name={"mail"}
                        >{i18n.t('editUser.labels.email')}</Form.ValidatedTextInput>
    <Form.ValidatedTextInput type={"password"} name={"password"}
                        >{i18n.t('editUser.labels.password')}</Form.ValidatedTextInput>
    <Form.ValidatedTextInput type={"password"} name={"confirmPassword"}
                        >{i18n.t('editUser.labels.confirmPassword')}</Form.ValidatedTextInput>
    <div className={"mt-4"}>
      <Button variant={"primary"} type={"submit"}
              disabled={!handler.dirty}>{i18n.t('editUser.buttons.save')}</Button>
    </div>
  </Form>
}
