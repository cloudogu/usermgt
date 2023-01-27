import * as Yup from "yup";
import useFormHandler from "@cloudogu/ces-theme-tailwind/src/components/forms/hooks/useFormHandler";
import Form from "@cloudogu/ces-theme-tailwind/src/components/forms/Form";
import H1 from "@cloudogu/ces-theme-tailwind/src/components/text/H1";
import TextInput from "@cloudogu/ces-theme-tailwind/src/components/inputs/TextInput";
import PrimaryButton from "@cloudogu/ces-theme-tailwind/src/components/inputs/PrimaryButton";

export default function Account() {
  const validationSchema = Yup.object({
    "surname": Yup.string().required('Surname is required.'),
    "displayname": Yup.string().required('Display name is required.'),
    "mail": Yup.string()
      .matches(/[a-zA-Z._-]*@[a-zA-Z-]*\.[a-zA-Z-]/, "E-mail address is invalid.")
      .required('Mail is required.'),
    "password": Yup.string()
      .matches(/[A-ZÄÖÜ]/, 'The password must contain at least 1 capital letter')
      .matches(/[0-9]/, 'The password must contain at least 1 number')
      .matches(/[^a-zäöüßA-ZÄÖÜ0-9]/, 'The password must contain at least 1 special character')
      .matches(/^.{8,}$/, 'The password must contain at least 8 characters')
      .matches(/[a-zäöüß]/, 'The password must contain at least 1 lower case letter'),
    "password_confirm": Yup.string().oneOf([Yup.ref('password'), null], 'Passwords must match'),
  });

  const handler = useFormHandler<any>({
    initialValues: {
      "username": "admin",
      "givenname": "Admin",
      "surname": "Admin",
      "displayname": "Admin Admin",
      "mail": "admin@admin.admin",
      "password": "adminpassword$A1",
      "password_confirm": "adminpassword$A1",
    },
    validationSchema: validationSchema,
    onSubmit: values => {
      alert(JSON.stringify(values, null, 2));
      handler.resetForm();
    },
  })

  return <Form handler={handler}>
    <H1>Account</H1>
    <TextInput name={"username"} handler={handler} disabled={true}>Username</TextInput>
    <TextInput name={"givenname"} handler={handler} showValidation={false}>Given name</TextInput>
    <TextInput name={"surname"} handler={handler}>Surname</TextInput>
    <TextInput name={"displayname"} handler={handler}>Display Name</TextInput>
    <TextInput name={"mail"} handler={handler}>E-Mail</TextInput>
    <TextInput name={"password"} handler={handler} isPassword={true}>Password</TextInput>
    <TextInput name={"password_confirm"} handler={handler} isPassword={true}>Confirm Password</TextInput>
    <div className={"mt-4"}>
      <PrimaryButton type={"submit"} disabled={!handler.dirty}>Save</PrimaryButton>
    </div>
  </Form>
}
