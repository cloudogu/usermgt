import React from 'react';
import {CesIconCheck, CesIconInfo, CesIconWarning, CesIconWarningDiamond} from "@cloudogu/ces-theme-tailwind";
import {t} from "../helpers/i18nHelpers";

export type MessageBoxProps = {
    text: string;
    icon?: string
    color?: string;
    className?: string
};

export function MessageBox({text, icon = 'info', color = 'brand', className = ''}: MessageBoxProps) {
    return <div className={`self-stretch p-2 bg-${color}-weaker rounded-md border-${color} border-2 inline-flex justify-between items-center mb-4 ${className}`}>
        <div className="flex-1 flex justify-start items-center gap-2">
            {icon === "info" ? <CesIconInfo className={`size-5 text-${color}`} aria-hidden={"true"}/> : ""}
            {icon === "check" ? <CesIconCheck className={`size-5 text-${color}`} aria-hidden={"true"}/> : ""}
            {icon === "warning" ? <CesIconWarning className={`size-5 text-${color}`} aria-hidden={"true"}/> : ""}
            {icon === "warning-diamond" ? <CesIconWarningDiamond className={`size-5 text-${color}`} aria-hidden={"true"}/> : ""}
            <div className={`flex-1 text-${color} text-base font-normal leading-6`}>
                {text}
            </div>
        </div>
    </div>
}

export default MessageBox;
