import * as Yup from "yup";
import {t} from "../helpers/i18nHelpers";

export function useGroupValidationSchema() {
    return Yup.object({
        "name": Yup.string()
            .required(t("newGroup.errors.name"))
            .min(2, t("newGroup.errors.nameMinLength"))
            .max(128, t("newGroup.errors.nameMaxLength"))
            .matches(/^[a-zA-Z0-9-_@\\.]*$/, t("newGroup.errors.nameFormat"))
    });
}
