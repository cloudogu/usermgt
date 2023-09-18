import i18n from "i18next";

export function t(_key: string): string;
export function t(_key: string, _options: any): string;

export function t(key: string, options?: any) {
    if(options) {
        // eslint-disable-next-line import/no-named-as-default-member
        return i18n.t(key, options) as unknown as string;
    }
    // eslint-disable-next-line import/no-named-as-default-member
    return i18n.t(key) as string;
}