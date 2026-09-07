import {CesIconCheck, CesIconX} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import "./DoguSelection.css";
import {createUseStyles} from "react-jss";

const useStyles = createUseStyles({
    fontBold600: {
        fontWeight: 600,
    },
    fontDefault400: {
        fontWeight: 400,
    },
});

export type DoguSelectionProps = {
    label: string;
    value: string[];
    onChange: (value: string[]) => void;
};

function radioButton(label: React.ReactNode, checked: boolean, onChange: () => void) {
    return (
        <div
            role="radio"
            aria-checked={checked}
            tabIndex={checked ? 0 : -1}
            onClick={() => { if (!checked) onChange(); }}
            onKeyDown={(event) => {
                const direction = event.key === "ArrowRight" || event.key === "ArrowDown" ? 1
                    : event.key === "ArrowLeft" || event.key === "ArrowUp" ? -1 : 0;
                if (direction !== 0) {
                    event.preventDefault();
                    const group = event.currentTarget.closest('[role="radiogroup"]');
                    if (!group) return;
                    const radios = Array.from(group.querySelectorAll<HTMLElement>('[role="radio"]'))
                        .filter(radio => radio.closest('[role="radiogroup"]') === group);
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
            className="focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-[-2px] cursor-pointer pt-3 pr-2 pb-3 pl-2 flex flex-row gap-2 items-start justify-start flex-1 min-h-[40px] relative overflow-hidden" >
            <div className="flex flex-row gap-0 items-start justify-start shrink-0 relative overflow-hidden" >
                <div className="shrink-0 w-6 h-6 relative">
                    <div className={`bg-default-background rounded-[50%] border-solid ${checked ? "border-brand border-2" : "border-neutral border"} w-6 h-6 absolute left-0 top-0`} />
                    {checked && <div className="bg-brand rounded-[50%] w-3.5 h-3.5 absolute left-[50%] top-[50%] -translate-x-1/2 -translate-y-1/2" />}
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

function checkBox(label: string, checked: boolean, disabled: boolean, onChange: () => void) {
    return (
        <div
            role="checkbox"
            aria-checked={checked}
            aria-disabled={disabled}
            tabIndex={disabled ? -1 : 0}
            onClick={() => { if (!disabled) onChange(); }}
            onKeyDown={(event) => {
                if (event.key === " " || event.key === "Enter") {
                    event.preventDefault();
                    if (!disabled) onChange();
                }
            }}
            className="group focus-visible:outline-none cursor-pointer aria-disabled:cursor-default pr-2 flex flex-row gap-0 items-center justify-start shrink-0 min-h-[40px] relative overflow-hidden" >
            <div className="p-2 flex flex-row gap-0 items-center justify-center shrink-0 min-h-[40px] relative overflow-hidden" >
                <div className="shrink-0 w-6 h-6 relative rounded group-focus-visible:outline group-focus-visible:outline-2 group-focus-visible:outline-offset-2 group-focus-visible:outline-[var(--ces-color-default-focus-outer)]">
                    <div className={`${checked ? "bg-brand border-brand" : "bg-neutral-colors-neutral-0 border-neutral"} rounded border-solid border w-6 h-6 absolute left-0 top-0`} />
                    {checked && <CesIconCheck className="text-inverted-text w-6 h-6 absolute left-0 top-0 overflow-visible" />}
                </div>
            </div>
            <div className="flex flex-row gap-0 items-center justify-start shrink-0 relative overflow-hidden" >
                <div className="text-default-text text-left font-lable-label-font-family text-lable-label-font-size leading-lable-label-line-height font-lable-label-font-weight relative">
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

function RadioGroupEntry({label, checked, onChange, selectedCount = 0, onClear, children, className = ""}: {
    label: React.ReactNode;
    checked: boolean;
    onChange: () => void;
    selectedCount?: number;
    onClear?: () => void;
    children?: React.ReactNode;
    className?: string;
}) {
    const hasChildren = React.Children.toArray(children).length > 0;
    const classes = useStyles();

    return (
        <div className={`rounded border-solid border ${checked ? "border-brand" : "border-neutral"} flex flex-col gap-0 items-start justify-start self-stretch shrink-0 relative ${className}`} >
            <div className={`${checked ? "bg-brand-weaker" : ""} rounded-tl-[5px] rounded-tr-[5px] ${hasChildren ? "pr-2" : "rounded"} flex flex-row gap-0 items-center justify-start self-stretch shrink-0 min-h-[40px] relative overflow-hidden`} >
                {radioButton(label, checked, onChange)}
            </div>
            {checked && hasChildren && (
                <div className="p-6 flex flex-col gap-4 items-end justify-start self-stretch shrink-0 relative overflow-hidden" >
                    <div className="bg-neutral-colors-neutral-0 rounded-rounded-sm flex flex-col gap-0 items-start justify-start self-stretch shrink-0 relative overflow-hidden" >
                        {children}
                    </div>
                    <div className="flex flex-row gap-4 gap-y-0 items-center justify-end flex-wrap content-center self-stretch shrink-0 relative" >
                        <div className="flex flex-row gap-2 items-center justify-start shrink-0 relative" >
                            <div className={`${classes.fontDefault400} text-neutral text-left font-copy-paragraph-regular-font-family text-copy-paragraph-regular-font-size leading-copy-paragraph-regular-line-height font-copy-paragraph-regular-font-weight relative`} >
                                {selectedCount} von 14 ausgewählt
                            </div>
                        </div>
                        <button type="button" disabled={!checked} onClick={onClear} className="rounded border-solid border-brand border-2 text-brand pt-1 pr-2 pb-1 pl-2 flex flex-row gap-05 items-center justify-center shrink-0 min-w-[40px] min-h-[40px] relative overflow-hidden" >
                            <div className="shrink-0 w-4 h-4 relative">
                                <CesIconX />
                            </div>
                            <div className="pr-2 pl-2 flex flex-row gap-0 items-center justify-center shrink-0 relative" >
                                <div className="text-brand text-left font-copy-paragraph-bold-font-family text-copy-paragraph-bold-font-size leading-copy-paragraph-bold-line-height font-copy-paragraph-bold-font-weight relative" >
                                    Auswahl aufheben
                                </div>
                            </div>
                        </button>
                    </div>
                </div>
            )}
        </div>
    );
}

function RadioGroup({label, children, className = ""}: {
    label: React.ReactNode;
    children?: React.ReactNode;
    className?: string;
}) {
    const labelId = React.useId();

    return (
        <div role="radiogroup" aria-labelledby={labelId} className={`desktop:text-desktop-regular mobile:text-mobile-regular flex flex-col gap-1 items-start justify-start self-stretch shrink-0 relative ${className}`} >
            <div className="flex flex-row gap-15 items-center justify-start shrink-0 relative overflow-hidden" >
                <div id={labelId} className="text-default-text text-left font-lable-label-font-family text-lable-label-font-size leading-lable-label-line-height font-lable-label-font-weight relative" >
                    {label}
                </div>
            </div>
            <div className="rounded-tl-[5px] rounded-tr-[5px] rounded-br-[5px] flex flex-col gap-2 items-start justify-start self-stretch shrink-0 relative" >
                {children}
            </div>
        </div>
    );
}

export function DoguSelection({label, value, onChange}: DoguSelectionProps) {
    const classes = useStyles();
    const allDogus = value.includes("/*");
    const selectedDogus = allDogus ? [] : value;
    const toggleDogu = (dogu: string) => {
        if (allDogus) return;
        onChange(selectedDogus.includes(dogu)
            ? selectedDogus.filter(selected => selected !== dogu)
            : [...selectedDogus, dogu]);
    };
    return (
        <RadioGroup label={label} className={classes.fontBold600}>
            <RadioGroupEntry label="Auswahl an Dogus" checked={!allDogus}
                onChange={() => onChange([])} selectedCount={selectedDogus.length} onClear={() => onChange([])}>
                <CheckBoxGroup label="Basis">
                    {checkBox("BlueSpice", selectedDogus.includes("BlueSpice"), allDogus, () => toggleDogu("BlueSpice"))}
                    {checkBox("Cockpit", selectedDogus.includes("Cockpit"), allDogus, () => toggleDogu("Cockpit"))}
                    {checkBox("Easy Redmine", selectedDogus.includes("Easy Redmine"), allDogus, () => toggleDogu("Easy Redmine"))}
                    {checkBox("LOP-Assistent", selectedDogus.includes("LOP-Assistent"), allDogus, () => toggleDogu("LOP-Assistent"))}
                    {checkBox("PMflexFLOW", selectedDogus.includes("PMflexFLOW"), allDogus, () => toggleDogu("PMflexFLOW"))}
                    {checkBox("Redmine", selectedDogus.includes("Redmine"), allDogus, () => toggleDogu("Redmine"))}
                </CheckBoxGroup>
                <CheckBoxGroup label="Entwicklung">
                    {checkBox("Grafana", selectedDogus.includes("Grafana"), allDogus, () => toggleDogu("Grafana"))}
                    {checkBox("Jenkins CI", selectedDogus.includes("Jenkins CI"), allDogus, () => toggleDogu("Jenkins CI"))}
                    {checkBox("SCM-Manager", selectedDogus.includes("SCM-Manager"), allDogus, () => toggleDogu("SCM-Manager"))}
                    {checkBox("Smeagol", selectedDogus.includes("Smeagol"), allDogus, () => toggleDogu("Smeagol"))}
                    {checkBox("SonarQube", selectedDogus.includes("SonarQube"), allDogus, () => toggleDogu("SonarQube"))}
                    {checkBox("Sonatype Nexus", selectedDogus.includes("Sonatype Nexus"), allDogus, () => toggleDogu("Sonatype Nexus"))}
                </CheckBoxGroup>
                <CheckBoxGroup label="Administration" className="pr-4">
                    {checkBox("Administration", selectedDogus.includes("Administration"), allDogus, () => toggleDogu("Administration"))}
                    {checkBox("User Management", selectedDogus.includes("User Management"), allDogus, () => toggleDogu("User Management"))}
                </CheckBoxGroup>
            </RadioGroupEntry>
            <RadioGroupEntry
                checked={allDogus}
                onChange={() => onChange(["/*"])}
                label={
                    <span>
                        <span className="label-4-span">Alle Dogus</span>
                        <span className={`label-4-span2 ${classes.fontDefault400}`}>
                            &nbsp;(inklusive nachträglich installierter)
                        </span>
                    </span>
                }
            />
        </RadioGroup>
    );
}


export default DoguSelection;
