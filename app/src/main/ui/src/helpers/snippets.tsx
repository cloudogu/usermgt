import {t} from "i18next";

export function requiredStar() {
    return <span aria-label={t("components.required.asterisk.hint")}>*</span>;
}