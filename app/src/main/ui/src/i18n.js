import i18n from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import {initReactI18next} from "react-i18next";

import de from "./i18n/de.json";
import en from "./i18n/en.json";

// eslint-disable-next-line import/no-named-as-default-member
i18n
    .use(LanguageDetector)
    .use(initReactI18next)
    .init({
        fallbackLng: "en",
        debug: false,
        resources: {
            de: {translation: {...de, ...(i18n.getResourceBundle?.call(undefined, "de", "translation") || {})}},
            en: {translation: {...en, ...(i18n.getResourceBundle?.call(undefined, "en", "translation") || {})}},
        },
        interpolation: {
            escapeValue: false,
        }
    })
    .then(/* ignored */);