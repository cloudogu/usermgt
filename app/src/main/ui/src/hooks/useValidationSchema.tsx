import {useEffect, useState} from "react";
import * as Yup from "yup";
import {ValidationError} from "yup";
import {t} from "../helpers/i18nHelpers";

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
  const passwordValidationFunction = (value: any) => {
    if (value === "__dummypassword") {
      return true;
    }

    const errors: ValidationError[] = [];

    for (const rule of passwordPolicy.Rules.filter(r => r.Type === "regex")) {
      const matches = new RegExp(rule.Rule).test(value as string);
      if (!matches) {
        let translatedErrorMessage: string = t(`editUser.errors.password.${rule.Name}`);
        for (const variable of rule.Variables) {
          translatedErrorMessage = t(translatedErrorMessage, {[variable.Name]: variable.Value})
        }
        errors.push(new ValidationError(translatedErrorMessage, "password", "password"));
      }
    }

    if (errors.length === 0) {
      return true;
    }

    return new ValidationError(errors);
  };

  return Yup.object({
    "surname": Yup.string().required(t('editUser.errors.surname') as string),
    "displayName": Yup.string().required(t('editUser.errors.displayName') as string),
    "mail": Yup.string()
      .matches(/[a-zA-Z._-]*@[a-zA-Z-]*\.[a-zA-Z-]/, t('editUser.errors.email.invalid') as string)
      .required(t('editUser.errors.email.required') as string),
    "password": Yup.string().test("", "", passwordValidationFunction),
    "confirmPassword": Yup.string().oneOf([Yup.ref('password'), null], t('editUser.errors.confirmPassword') as string),
  });
}
