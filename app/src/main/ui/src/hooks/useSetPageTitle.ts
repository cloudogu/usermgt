import {useEffect} from "react";
import {t} from "../helpers/i18nHelpers";

export function useSetPageTitle(title: string) {
    useEffect(() => {
        (document.title = `${title} | ${t("general.applicationName")}`);
    }, [title]);
}