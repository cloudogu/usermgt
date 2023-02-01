import {Button, Form, H1, useFormHandler, ValidatedTextInput} from "@cloudogu/ces-theme-tailwind";
import {ApiAccount} from "../../hooks/useAccount";


type AccountFormProps = {
    account: ApiAccount
    validationSchema: any

}

export default function AccountForm(props: AccountFormProps) {
    const handler = useFormHandler<any>({
        initialValues: props.account,
        validationSchema: props.validationSchema,
        onSubmit: values => {
            alert(JSON.stringify(values, null, 2));
            handler.resetForm();
        },
    })
    return <Form handler={handler}>
        <H1>Account</H1>
        <ValidatedTextInput type={"text"} name={"username"} handler={handler}
                            disabled={true}>Username</ValidatedTextInput>
        <ValidatedTextInput type={"text"} name={"givenName"} handler={handler}>Given name</ValidatedTextInput>
        <ValidatedTextInput type={"text"} name={"surname"} handler={handler}>Surname</ValidatedTextInput>
        <ValidatedTextInput type={"text"} name={"displayName"} handler={handler}>Display Name</ValidatedTextInput>
        <ValidatedTextInput type={"text"} name={"mail"} handler={handler}>E-Mail</ValidatedTextInput>
        <ValidatedTextInput type={"password"} name={"password"} handler={handler}>Password</ValidatedTextInput>
        <ValidatedTextInput type={"password"} name={"password_confirm"} handler={handler}>Confirm
            Password</ValidatedTextInput>
        <div className={"mt-4"}>
            <Button variant={"primary"} type={"submit"} disabled={!handler.dirty}>Save</Button>
        </div>
    </Form>
}