import {useEffect, useState} from "react";
import * as Yup from "yup";
import {useTranslation} from "react-i18next";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

export type PasswordPolicy = {
  Rules: { Rule: string, Type: "regex", Name: string, Variables: { Name: string; Value: string }[] }[],
}

export function useValidationSchema(): any {
  const [passwordPolicy, setPasswordPolicy] = useState<PasswordPolicy>({Rules: []});

  useEffect(() => {
    fetch(contextPath + `/api/account/passwordpolicy`)
      .then(async function (response) {
        const json: PasswordPolicy = await response.json();
        setPasswordPolicy(json);
      });
  }, []);

  return createValidationSchema(passwordPolicy);
}

function createValidationSchema(passwordPolicy: PasswordPolicy) {
  const {t} = useTranslation<string>();
  let passwordConfig = Yup.string();
  for (const rule of passwordPolicy.Rules.filter(r => r.Type === "regex")) {
    let translatedErrorMessage: string = t(`editUser.errors.password.${rule.Name}`);
    for (const variable of rule.Variables) {
      translatedErrorMessage = translatedErrorMessage.replace(`{{${variable.Name}}}`, variable.Value)
    }
    passwordConfig = passwordConfig.matches(new RegExp(rule.Rule), translatedErrorMessage);
  }

  return Yup.object({
    "surname": Yup.string().required('Surname is required.'),
    "displayName": Yup.string().required('Display name is required.'),
    "mail": Yup.string()
      .matches(/[a-zA-Z._-]*@[a-zA-Z-]*\.[a-zA-Z-]/, "E-mail address is invalid.")
      .required('Mail is required.'),
    "password": passwordConfig,
    "password_confirm": Yup.string().oneOf([Yup.ref('password'), null], 'Passwords must match'),
  });
}
