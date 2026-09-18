import {Button, CheckboxField, CesIconX, CesIconWarning} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {t, tWithParams} from "../../helpers/i18nHelpers";
import useDoguSelectionStyles from "../../hooks/useDoguSelectionStyles";
import {useDogus} from "../../hooks/useDogus";
import type {DoguOption} from "../../services/Dogus";

export type DoguSelectionProps = {
    label: string;
    value: string[];
    onChange: (_value: string[]) => void;
    invalid?: boolean;
};

function radioButton(label: React.ReactNode, checked: boolean, onChange: () => void, testId?: string) {
    return (
        <div
            role="radio"
            data-testid={testId}
            aria-checked={checked}
            tabIndex={0}
            onClick={() => { if (!checked) onChange(); }}
            onKeyDown={(event) => {
                const direction = event.key === "ArrowRight" || event.key === "ArrowDown" ? 1
                    : event.key === "ArrowLeft" || event.key === "ArrowUp" ? -1 : 0;
                if (direction !== 0) {
                    event.preventDefault();
                    const group = event.currentTarget.closest("[role=\"radiogroup\"]");
                    if (!group) return;
                    const radios = Array.from(group.querySelectorAll<HTMLElement>("[role=\"radio\"]"))
                        .filter(radio => radio.closest("[role=\"radiogroup\"]") === group);
                    const index = radios.indexOf(event.currentTarget);
                    const nextRadio = radios[(index + direction + radios.length) % radios.length];
                    nextRadio.focus();
                    nextRadio.click();
                    return;
                }
                if (event.key === " " || event.key === "Enter") {
                    event.preventDefault();
                    if (!checked) onChange();
                }
            }}
            className="group focus-visible:outline-none cursor-pointer pt-3 pr-2 pb-3 pl-2 flex flex-row gap-2 items-start justify-start flex-1 min-h-[40px] relative overflow-hidden" >
            <div className="flex flex-row gap-0 items-start justify-start shrink-0 relative overflow-visible" >
                <div className="shrink-0 w-6 h-6 relative rounded-full group-focus-visible:outline group-focus-visible:outline-2 group-focus-visible:outline-offset-2 group-focus-visible:outline-[var(--ces-color-default-focus-outer)]">
                    <div className={`bg-default-background rounded-[50%] border-solid ${checked ? "border-brand border-2 hover-dark-border" : "border-neutral border hover-neutral-border"} w-6 h-6 absolute left-0 top-0`} />
                    {checked && <div className="bg-brand rounded-[50%] w-3.5 h-3.5 absolute left-[50%] top-[50%] -translate-x-1/2 -translate-y-1/2 hover-dark-bg" />}
                </div>
            </div>
            <div className="flex flex-row gap-0 items-center justify-start shrink-0 relative overflow-hidden" >
                <div className="text-default-text text-left font-lable-label-font-family text-lable-label-font-size leading-lable-label-line-height font-lable-label-font-weight relative" >
                    {label}
                </div>
            </div>
        </div>
    );
}

function CheckBoxGroup({label, children, className = ""}: {
    label: React.ReactNode;
    children: React.ReactNode;
    className?: string;
}) {
    return (
        <div className="self-stretch shrink-0 pt-4 first:pt-0 border-t first:border-t-0 border-neutral-weak">
            <div className="pr-2 pb-4 pl-2 flex flex-row items-center justify-between self-stretch shrink-0 relative overflow-hidden">
                <div className="text-default-text text-left font-copy-paragraph-bold-font-family text-copy-paragraph-bold-font-size leading-copy-paragraph-bold-line-height font-copy-paragraph-bold-font-weight relative">
                    {label}
                </div>
            </div>
            <div className={`pb-4 flex flex-col gap-2 items-start justify-start self-stretch shrink-0 relative overflow-hidden ${className}`}>
                {children}
            </div>
        </div>
    );
}

function RadioGroupEntry({testId, label, checked, onChange, selectedCount = 0, totalCount = 0, onClear, children, className = ""}: {
    label: React.ReactNode;
    testId?: string;
    checked: boolean;
    onChange: () => void;
    selectedCount?: number;
    totalCount?: number;
    onClear?: () => void;
    children?: React.ReactNode;
    className?: string;
}) {
    const hasChildren = React.Children.toArray(children).length > 0;
    const classes = useDoguSelectionStyles();

    return (
        <div className={`rounded border-solid border ${checked ? "border-brand" : "border-neutral"} flex flex-col gap-0 items-start justify-start self-stretch shrink-0 relative ${className}`} >
            <div className={`${checked ? "bg-brand-weaker" : ""} rounded-tl-[5px] rounded-tr-[5px] ${hasChildren ? "pr-2" : "rounded"} flex flex-row gap-0 items-center justify-start self-stretch shrink-0 min-h-[40px] relative overflow-hidden ${classes.doguSelectionHover}`} >
                {radioButton(label, checked, onChange, testId)}
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

function RadioGroup({label, children, className = "", invalid = false}: {
    invalid?: boolean;
    label: React.ReactNode;
    children?: React.ReactNode;
    className?: string;
}) {
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

export function DoguSelection({label, value, onChange, invalid = false}: DoguSelectionProps) {
    const classes = useDoguSelectionStyles();
    const {doguOptions} = useDogus();
    const administrationDogus: DoguOption[] = [];
    const developmentDogus: DoguOption[] = [];
    const basicDogus: DoguOption[] = [];
    doguOptions.forEach(dogu => {
        if (dogu.category === "Administration" || dogu.category === "Administration Apps") {
            administrationDogus.push(dogu);
        } else if (dogu.category === "Development" || dogu.category === "Development Apps") {
            developmentDogus.push(dogu);
        } else {
            basicDogus.push(dogu);
        }
    });
    const allDogus = value.includes("/*");
    const selectedDogus = allDogus ? [] : value;
    const toggleDogu = (dogu: string) => {
        if (allDogus) return;
        onChange(selectedDogus.includes(dogu)
            ? selectedDogus.filter(selected => selected !== dogu)
            : [...selectedDogus, dogu]);
    };
    const renderDogu = (dogu: DoguOption) => (
        <CheckboxField
            key={dogu.value}
            data-testid={`security-pat-dogu-${dogu.value}`}
            checked={selectedDogus.includes(dogu.value)}
            disabled={allDogus}
            onCheckedChange={() => toggleDogu(dogu.value)}
            className={`p-2 min-h-[40px] items-center ${classes.checkboxFocus} text-default-text`}
        >
            {dogu.label}
        </CheckboxField>
    );
    return (
        <RadioGroup label={label} className={classes.fontBold600} invalid={invalid}>
            <RadioGroupEntry testId="security-pat-selected-dogus" label="Auswahl an Dogus" checked={!allDogus}
                onChange={() => onChange([])} selectedCount={selectedDogus.length} totalCount={doguOptions.length} onClear={() => onChange([])}>
                {basicDogus.length > 0 && (
                    <CheckBoxGroup label={t("security.createpat.check.base")}>
                        {basicDogus.map(renderDogu)}
                    </CheckBoxGroup>
                )}
                {developmentDogus.length > 0 && (
                    <CheckBoxGroup label={t("security.createpat.check.development")}>
                        {developmentDogus.map(renderDogu)}
                    </CheckBoxGroup>
                )}
                {administrationDogus.length > 0 && (
                    <CheckBoxGroup label={t("security.createpat.check.administration")} className="pr-4">
                        {administrationDogus.map(renderDogu)}
                    </CheckBoxGroup>
                )}
            </RadioGroupEntry>
            <RadioGroupEntry
                testId="security-pat-all-dogus"
                checked={allDogus}
                onChange={() => onChange(["/*"])}
                label={
                    <span>
                        <span>{t("security.createpat.scopes.selectdogus.all.label.dogus")}</span>
                        <span className={classes.fontDefault400}>
                            &nbsp;({t("security.createpat.scopes.selectdogus.all.label.hint")})
                        </span>
                    </span>
                }
            />
        </RadioGroup>
    );
}


export default DoguSelection;
