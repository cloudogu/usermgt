import {CesIconCheck} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import "./DoguSelection.css";


export type DoguSelectionProps = {
    label: string;
};

function radioButton(label: React.ReactNode, checked: boolean) {
    return (
        <div className="pt-3 pr-2 pb-3 pl-2 flex flex-row gap-2 items-start justify-start flex-1 min-h-[40px] relative overflow-hidden" >
            <div className="flex flex-row gap-0 items-start justify-start shrink-0 relative overflow-hidden" >
                <div className="shrink-0 w-6 h-6 relative">
                    <div className={`bg-neutral-colors-neutral-0 rounded-[50%] border-solid ${checked ? "border-brand-colors-brand-brand-700 border-2" : "border-neutral-colors-neutral-neutral-600 border"} w-6 h-6 absolute left-0 top-0`} />
                    {checked && <div className="bg-brand-colors-brand-brand-700 rounded-[50%] w-3.5 h-3.5 absolute left-[50%] top-[50%] -translate-x-1/2 -translate-y-1/2" />}
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

function checkBox(label: React.ReactNode, checked: boolean) {
    return (
        <div className="pr-2 flex flex-row gap-0 items-center justify-start shrink-0 min-h-[40px] relative overflow-hidden" >
            <div className="p-2 flex flex-row gap-0 items-center justify-center shrink-0 min-h-[40px] relative overflow-hidden" >
                <div className="shrink-0 w-6 h-6 relative">
                    <div className={`${checked ? "bg-brand-colors-brand-brand-700 border-brand-colors-brand-brand-700" : "bg-neutral-colors-neutral-0 border-neutral-colors-neutral-neutral-600"} rounded-rounded-xs border-solid border w-6 h-6 absolute left-0 top-0`} />
                    {checked && <CesIconCheck className="w-6 h-6 absolute left-0 top-0 overflow-visible" />}
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
        <div className="self-stretch shrink-0 pt-4 first:pt-0 border-t first:border-t-0 border-neutral-colors-neutral-weak-neutral-300">
            <div className="bg-neutral-colors-neutral-0 pr-2 pb-4 pl-2 flex flex-row items-center justify-between self-stretch shrink-0 relative overflow-hidden">
                <div className="text-default-text text-left font-copy-paragraph-bold-font-family text-copy-paragraph-bold-font-size leading-copy-paragraph-bold-line-height font-copy-paragraph-bold-font-weight relative">
                    {label}
                </div>
            </div>
            <div className={`bg-neutral-colors-neutral-0 pb-4 flex flex-col gap-2 items-start justify-start self-stretch shrink-0 relative overflow-hidden ${className}`}>
                {children}
            </div>
        </div>
    );
}

function RadioGroupEntry({label, children, className = ""}: {
    label: React.ReactNode;
    children: React.ReactNode;
    className?: string;
}) {
    return (
        <div className={`flex flex-col gap-0 items-start justify-start self-stretch shrink-0 relative ${className}`} >
            <div className="bg-brand-colors-brand-weaker-brand-100 rounded-tl-rounded-lg rounded-tr-rounded-lg border-solid border-brand-colors-brand-brand-700 border-t border-r border-l pr-2 flex flex-row gap-0 items-center justify-start self-stretch shrink-0 min-h-[40px] relative overflow-hidden" >
                {radioButton(label, true)}
            </div>
            <div className="bg-neutral-colors-neutral-0 rounded-br-rounded-lg rounded-bl-rounded-lg border-solid border-brand-colors-brand-brand-700 border-r border-b border-l p-6 flex flex-col gap-4 items-end justify-start self-stretch shrink-0 relative overflow-hidden" >
                <div className="bg-neutral-colors-neutral-0 rounded-rounded-sm flex flex-col gap-0 items-start justify-start self-stretch shrink-0 relative overflow-hidden" >
                    {children}
                </div>
                <div className="flex flex-row gap-4 gap-y-0 items-center justify-end flex-wrap content-center self-stretch shrink-0 relative" >
                    <div className="flex flex-row gap-2 items-center justify-start shrink-0 relative" >
                        <div className="text-neutral-colors-neutral-neutral-600 text-left font-copy-paragraph-regular-font-family text-copy-paragraph-regular-font-size leading-copy-paragraph-regular-line-height font-copy-paragraph-regular-font-weight relative" >
                            3 von 14 ausgewählt
                        </div>
                    </div>
                    <div className="rounded-rounded-sm border-solid border-brand-colors-brand-brand-700 border-2 pt-2 pr-25 pb-2 pl-25 flex flex-row gap-05 items-center justify-center shrink-0 min-w-[40px] min-h-[40px] relative overflow-hidden" >
                        <div className="shrink-0 w-4 h-4 relative">
                            <img
                                className="w-4 h-4 absolute left-0 top-0 overflow-visible"
                                src="x0.svg"
                            />
                        </div>
                        <div
                            className="pr-15 pl-15 flex flex-row gap-0 items-center justify-center shrink-0 relative"
                        >
                            <div
                                className="text-brand-colors-brand-brand-700 text-left font-copy-paragraph-bold-font-family text-copy-paragraph-bold-font-size leading-copy-paragraph-bold-line-height font-copy-paragraph-bold-font-weight relative"
                            >
                                Auswahl aufheben
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}

export function DoguSelection({label}: DoguSelectionProps) {
    return (
        <div className="flex flex-col gap-1 items-start justify-start self-stretch shrink-0 relative" >
            <div className="flex flex-row gap-15 items-center justify-start shrink-0 relative overflow-hidden" >
                <div className="text-default-text text-left font-lable-label-font-family text-lable-label-font-size leading-lable-label-line-height font-lable-label-font-weight relative" >
                    {label}
                </div>
            </div>
            <div className="rounded-tl-[5px] rounded-tr-[5px] rounded-br-[5px] flex flex-col gap-2 items-start justify-start self-stretch shrink-0 relative" >
                <RadioGroupEntry label="Auswahl an Dogus">
                    <CheckBoxGroup label="Basis">
                        {checkBox("BlueSpice", true)}
                        {checkBox("Cockpit", false)}
                        {checkBox("Easy Redmine", false)}
                        {checkBox("LOP-Assistent", true)}
                        {checkBox("PMflexFLOW", false)}
                        {checkBox("Redmine", false)}
                    </CheckBoxGroup>
                    <CheckBoxGroup label="Entwicklung">
                        {checkBox("Grafana", true)}
                        {checkBox("Jenkins CI", false)}
                        {checkBox("SCM-Manager", false)}
                        {checkBox("Smeagol", false)}
                        {checkBox("SonarQube", false)}
                        {checkBox("Sonatype Nexus", false)}
                    </CheckBoxGroup>
                    <CheckBoxGroup label="Administration" className="pr-4">
                        {checkBox("Administration", false)}
                        {checkBox("User Management", false)}
                    </CheckBoxGroup>
                </RadioGroupEntry>
                <div
                    className="rounded-rounded-lg border-solid border-neutral-colors-neutral-weak-neutral-300 border flex flex-row gap-2 items-start justify-start self-stretch shrink-0 min-h-[40px] relative overflow-hidden"
                >
                    {radioButton(
                        <span>
                            <span className="label-4-span">Alle Dogus</span>
                            <span className="label-4-span2">
                                (inklusive nachträglich installierter)
                            </span>
                        </span>,
                        false
                    )}
                </div>
            </div>
        </div>

    );
}


export default DoguSelection;
