import React from "react";
import {useRadioGroupName} from "./RadioGroupContext";

export type RadioButtonProps = {
    label: React.ReactNode;
    checked: boolean;
    onChange: () => void;
    testId?: string;
};

export function RadioButton({label, checked, onChange, testId}: RadioButtonProps) {
    const name = useRadioGroupName();

    return (
        <label className="cursor-pointer py-3 px-2 flex flex-row gap-2 items-start justify-start flex-1 min-h-[40px] relative overflow-hidden" >
            <input
                type="radio"
                name={name}
                checked={checked}
                onChange={onChange}
                data-testid={testId}
                className="peer sr-only"
            />
            <span aria-hidden="true" className="block shrink-0 w-6 h-6 relative rounded-full peer-focus-visible:outline peer-focus-visible:outline-2 peer-focus-visible:outline-offset-2 peer-focus-visible:outline-[var(--ces-color-default-focus-outer)]">
                <span className={`bg-default-background rounded-[50%] border-solid ${checked ? "border-brand border-2 hover-dark-border" : "border-neutral border hover-neutral-border"} w-6 h-6 absolute left-0 top-0`} />
                {checked && <span className="bg-brand rounded-[50%] w-3.5 h-3.5 absolute left-[50%] top-[50%] -translate-x-1/2 -translate-y-1/2 hover-dark-bg" />}
            </span>
            <span className="flex flex-row gap-0 items-center justify-start relative" >
                <span className="text-default-text text-left" >
                    {label}
                </span>
            </span>
        </label>
    );
}
