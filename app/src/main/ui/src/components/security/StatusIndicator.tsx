import React from "react";
import {t} from "../../helpers/i18nHelpers";

export type StatusIndicatorProps = {
    text: string;
};

export function StatusIndicator({text}: StatusIndicatorProps) {
    const isActive = text === "active";

    return (
        <span className={`rounded-full px-2 py-1 h-5 desktop:text-desktop-small mobile:text-mobile-small ${isActive ? "bg-brand-weaker" : "bg-neutral-weak"}`}>
            {t(isActive ? "security.overview.table.statustype.active" : "security.overview.table.statustype.expired")}
        </span>
    );
}

export default StatusIndicator;
