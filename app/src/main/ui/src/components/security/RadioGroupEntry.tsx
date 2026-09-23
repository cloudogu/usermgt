import {Button, CesIconX} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {t, tWithParams} from "../../helpers/i18nHelpers";
import useDoguSelectionStyles from "../../hooks/useDoguSelectionStyles";
import {RadioButton} from "./RadioButton";

export type RadioGroupEntryProps = {
    label: React.ReactNode;
    testId?: string;
    checked: boolean;
    onChange: () => void;
    selectedCount?: number;
    totalCount?: number;
    onClear?: () => void;
    children?: React.ReactNode;
    className?: string;
};

export function RadioGroupEntry({testId, label, checked, onChange, selectedCount = 0, totalCount = 0, onClear, children, className = ""}: RadioGroupEntryProps) {
    const hasChildren = React.Children.toArray(children).length > 0;
    const classes = useDoguSelectionStyles();

    return (
        <div className={`rounded border-solid border ${checked ? "border-brand" : "border-neutral"} flex flex-col gap-0 items-start justify-start self-stretch shrink-0 relative ${className}`} >
            <div className={`${checked ? "bg-brand-weaker" : ""} rounded-tl-[5px] rounded-tr-[5px] ${hasChildren ? "pr-2" : "rounded"} flex flex-row gap-0 items-center justify-start self-stretch shrink-0 min-h-[40px] relative overflow-hidden ${classes.doguSelectionHover}`} >
                <RadioButton label={label} checked={checked} onChange={onChange} testId={testId} />
            </div>
            {checked && hasChildren && (
                <div className="p-6 flex flex-col gap-4 items-end justify-start self-stretch shrink-0 relative overflow-hidden" >
                    <div className="bg-neutral-colors-neutral-0 rounded-rounded-sm flex flex-col gap-0 items-start justify-start self-stretch shrink-0 relative overflow-hidden" >
                        {children}
                    </div>
                    <div className="flex flex-row gap-4 gap-y-0 items-center justify-end flex-wrap content-center self-stretch shrink-0 relative" >
                        <div className="flex flex-row gap-2 items-center justify-start shrink-0 relative" >
                            <div className={`${classes.fontDefault400} text-neutral text-left font-copy-paragraph-regular-font-family text-copy-paragraph-regular-font-size leading-copy-paragraph-regular-line-height font-copy-paragraph-regular-font-weight relative`} >
                                {tWithParams("security.createpat.selectdogus.hint", selectedCount, totalCount)}
                            </div>
                        </div>

                        <Button type="button" variant="secondary" color="brand" disabled={!checked}
                            onClick={onClear}
                            className={`${classes.checkboxFocus} flex flex-row items-center gap-1 whitespace-nowrap`}>
                            <CesIconX /> {t("security.createpat.selectdogus.clearselection")}
                        </Button>
                    </div>
                </div>
            )}
        </div>
    );
}
