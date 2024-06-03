import * as Yup from "yup";
import {ValidationError} from "yup";
import {t} from "../helpers/i18nHelpers";
import {defaultPasswordPolicy, ValidationSchemaService} from "../services/ValidationSchema";
import {useAPI} from "./useAPI";
import type { PasswordPolicy} from "../services/ValidationSchema";

export function useValidationSchema(): any {
    const {data:passwordPolicy} = useAPI<PasswordPolicy>(ValidationSchemaService.get);
    return createValidationSchema(passwordPolicy ?? defaultPasswordPolicy);
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
                    translatedErrorMessage = t(translatedErrorMessage, {[variable.Name]: variable.Value});
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
        "username": Yup.string()
            .matches(/^.{2,}$/, t("editUser.errors.username.minlength") as string)
            .matches(/^.{0,128}$/, t("editUser.errors.username.maxlength") as string)
            .matches(/^[a-zA-Z0-9-_@.]*$/, t("editUser.errors.username.invalid") as string)
            .required(t("editUser.errors.username.required") as string),
        "givenname": Yup.string().required(t("editUser.errors.givenname") as string),
        "surname": Yup.string().required(t("editUser.errors.surname") as string),
        "displayName": Yup.string().required(t("editUser.errors.displayName") as string),
        "mail": Yup.string()
            // simple email validation was chosen after discussing Internationalized domain name (öäü)
            .matches(/.+@.+/, t("editUser.errors.email.invalid") as string)
            .required(t("editUser.errors.email.required") as string),
        "password": Yup.string()
            .test("", "", passwordValidationFunction)
            .matches(/^.*\S.*$/, t("editUser.errors.password.nonWhitespace") as string),
        "confirmPassword": Yup.string().oneOf([Yup.ref("password"), null], t("editUser.errors.confirmPassword") as string),
    });
}
