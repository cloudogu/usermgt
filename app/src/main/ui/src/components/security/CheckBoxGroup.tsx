import React from "react";

export type CheckBoxGroupProps = {
    label: React.ReactNode;
    children: React.ReactNode;
    className?: string;
};

export function CheckBoxGroup({label, children, className = ""}: CheckBoxGroupProps) {
    return (
        <div className="self-stretch shrink-0 pt-4 first:pt-0 border-t first:border-t-0 border-neutral-weak">
            <div className="pr-2 pb-4 pl-2 flex flex-row items-center justify-between self-stretch shrink-0 relative overflow-hidden">
                <div className="text-default-text text-left font-bold">
                    {label}
                </div>
            </div>
            <div className={`pb-4 flex flex-col gap-2 items-start justify-start self-stretch shrink-0 relative overflow-hidden ${className}`}>
                {children}
            </div>
        </div>
    );
}
