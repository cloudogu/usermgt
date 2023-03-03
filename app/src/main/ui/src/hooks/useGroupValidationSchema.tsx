import * as Yup from "yup";
import {t} from "../helpers/i18nHelpers";

export function useGroupValidationSchema(): any {
    return Yup.object({
        "name": Yup.string().required(t("newGroup.errors.name")).min(2, t("newGroup.errors.nameLength"))
    });
}
