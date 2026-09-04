import {translateToPlainString} from "@cloudogu/ces-theme-tailwind";

export function pageTitle(key: string): string {
    return `${translateToPlainString(key)}`;
}
