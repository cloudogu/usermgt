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

/**
 * Translates a key and replaces printf-style placeholders in the translated
 * text with the supplied parameters.
 */
export function tWithParams(key: string, ...params: unknown[]): string {
    let parameterIndex = 0;

    return t(key).replace(/%[sdif]/g, placeholder => {
        const parameter = params[parameterIndex++];
        if (parameter === undefined) {
            return placeholder;
        }

        switch (placeholder) {
            case "%d":
            case "%i":
                return String(Number.parseInt(String(parameter), 10));
            case "%f":
                return String(Number.parseFloat(String(parameter)));
            case "%s":
            default:
                return String(parameter);
        }
    });
}

export function formatDate(value: string): string {
    if (!value || value === "-") {
        return t("security.overview.table.status.noexpiration.option");
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
        return t("security.overview.table.status.noexpiration.option");
    }

    return new Intl.DateTimeFormat(i18n.language, {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
    }).format(date);
}
