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
    const classes = useDoguSelectionStyles();

    return (
        <div role="radiogroup" aria-invalid={invalid} aria-labelledby={labelId} className={`desktop:text-desktop-regular mobile:text-mobile-regular flex flex-col gap-1 items-start justify-start self-stretch shrink-0 relative ${className}`} >
            <div className="flex flex-row gap-15 items-center justify-start shrink-0 relative overflow-hidden" >
                <div id={labelId} className={`${invalid ? "text-danger" : "text-default-text"} text-left font-lable-label-font-family text-lable-label-font-size leading-lable-label-line-height font-lable-label-font-weight relative`}>
                    <span className="inline-flex items-center gap-1.5">
                        {invalid ? <CesIconWarning/> : ""}
                        {label}
                    </span>
                    {invalid ? <span className={["block", "desktop:text-desktop-small", "mobile:text-mobile-small", "text-danger", classes.fontDefault400].join(" ")}>{t("security.createpat.error.dogus.select")}</span> : ""}
                </div>
            </div>
            <div className="rounded-tl-[5px] rounded-tr-[5px] rounded-br-[5px] flex flex-col gap-2 items-start justify-start self-stretch shrink-0 relative" >
                {children}
            </div>
        </div>
    );
}
