import {CesIconWarning} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {t} from "../../helpers/i18nHelpers";
import useDoguSelectionStyles from "../../hooks/useDoguSelectionStyles";

export type RadioGroupProps = {
    invalid?: boolean;
    label: React.ReactNode;
    children?: React.ReactNode;
    className?: string;
};

export function RadioGroup({label, children, className = "", invalid = false}: RadioGroupProps) {
    const labelId = React.useId();
    const errorId = React.useId();
    const classes = useDoguSelectionStyles();

    return (
        <fieldset role="radiogroup" aria-invalid={invalid} aria-labelledby={labelId} aria-describedby={invalid ? errorId : undefined} className={`desktop:text-desktop-regular mobile:text-mobile-regular min-w-0 flex flex-col gap-1 items-start justify-start self-stretch shrink-0 relative ${className}`} >
            <legend id={labelId} className={`${invalid ? "text-danger" : "text-default-text"} mb-1 text-left`}>
                <span className="inline-flex items-center gap-1.5">
                    {invalid ? <CesIconWarning aria-hidden="true"/> : ""}
                    {label}
                </span>
            </legend>
            {invalid && (
                <span id={errorId} className={["block", "desktop:text-desktop-small", "mobile:text-mobile-small", "text-danger", classes.fontDefault400].join(" ")}>
                    {t("security.createpat.error.dogus.select")}
                </span>
            )}
            <div className="rounded-tl-[5px] rounded-tr-[5px] rounded-br-[5px] flex flex-col gap-2 items-start justify-start self-stretch shrink-0 relative" >
                {children}
            </div>
        </fieldset>
    );
}
