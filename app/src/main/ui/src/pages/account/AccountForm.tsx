import {Alert, Button, Form, H1, useFormHandler, ValidatedTextInput} from "@cloudogu/ces-theme-tailwind";
import {ApiAccount, putAccount} from "../../hooks/useAccount";
import {useState} from "react";
import i18n from 'i18next';


type AccountFormProps = {
  account: ApiAccount;
  validationSchema: any;
  setAccount: (account: ApiAccount) => void;
}

export default function AccountForm(props: AccountFormProps) {
  const handler = useFormHandler<any>({
    initialValues: props.account,
    validationSchema: props.validationSchema,
    enableReinitialize: true,
    onSubmit: values => {
      putAccount(values).catch(error => {
        setAlert(<Alert variant={"danger"} onClose={() => {
          setAlert(<></>)
        }}>{error}</Alert>)
      }).then(value => {
        if (value !== undefined) {
          setAlert(<Alert variant={"primary"} onClose={() => {
            setAlert(<></>)
          }}>{value}</Alert>)
        }
        props.setAccount(values);
        handler.resetForm(values);
      });
    },
  });
  const [alert, setAlert] = useState(<></>)
  return <Form handler={handler}>
    <H1>Account</H1>
    {alert}
    <ValidatedTextInput type={"text"} name={"username"} handler={handler}
                        disabled={true}>{i18n.t('editUser.labels.username')}</ValidatedTextInput>
    <ValidatedTextInput type={"text"} name={"givenname"}
                        handler={handler}>{i18n.t('editUser.labels.givenName')}</ValidatedTextInput>
    <ValidatedTextInput type={"text"} name={"surname"}
                        handler={handler}>{i18n.t('editUser.labels.surname')}</ValidatedTextInput>
    <ValidatedTextInput type={"text"} name={"displayName"}
                        handler={handler}>{i18n.t('editUser.labels.displayName')}</ValidatedTextInput>
    <ValidatedTextInput type={"text"} name={"mail"}
                        handler={handler}>{i18n.t('editUser.labels.email')}</ValidatedTextInput>
    <ValidatedTextInput type={"password"} name={"password"}
                        handler={handler}>{i18n.t('editUser.labels.password')}</ValidatedTextInput>
    <ValidatedTextInput type={"password"} name={"confirmPassword"}
                        handler={handler}>{i18n.t('editUser.labels.confirmPassword')}</ValidatedTextInput>
    <div className={"mt-4"}>
      <Button variant={"primary"} type={"submit"}
              disabled={!handler.dirty}>{i18n.t('editUser.buttons.save')}</Button>
    </div>
  </Form>
}
