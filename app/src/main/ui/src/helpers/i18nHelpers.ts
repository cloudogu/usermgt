import i18n from "i18next";

export function t(key: string): string;
export function t(key: string, options: any): string;

export function t(key: string, options?: any) {
    if(options) {
        return i18n.t(key, options) as unknown as string;
    }
    return i18n.t(key) as string;
}