import i18n from "i18next";
import { Trans } from "react-i18next";

export function translate(_key: string): string;
export function translate(_key: string, _options: any): string;

export function translate(key: string, options?: any) {
    if(options) {
        return i18n.t(key, options) as unknown as string;
    }
    return i18n.t(key) as string;
}

export function getLocale(): string {
    return i18n.language;
}

/**
 * translateWithEnglishWords can be used to mark English words within a non English translation using the tag <en/>
 * Example: de-Translation has the following key
 *  key: "Der Button wird zum <en>Logout</en> verwendet"
 *  output: "Der Button wird zum <span lang="en">Logout</span> verwendet"
 * @param key within i18n translation
 * @returns Translation with embedded html tag
 */
export function translateWithEnglishWords(key: string, placesholders?: any) {
  return (
    <Trans 
        i18nKey={key}
        values={placesholders}
        components={{ en: <span lang="en" /> }} />
  );
}