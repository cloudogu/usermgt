import React from "react";

export type RadioButtonProps = {
    label: React.ReactNode;
    checked: boolean;
    onChange: () => void;
    testId?: string;
};

export function RadioButton({label, checked, onChange, testId}: RadioButtonProps) {
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
            className="group focus-visible:outline-none cursor-pointer py-3 px-2 flex flex-row gap-2 items-start justify-start flex-1 min-h-[40px] relative overflow-hidden" >
            <div className="flex flex-row gap-0 items-start justify-start shrink-0 relative overflow-visible" >
                <div className="shrink-0 w-6 h-6 relative rounded-full group-focus-visible:outline group-focus-visible:outline-2 group-focus-visible:outline-offset-2 group-focus-visible:outline-[var(--ces-color-default-focus-outer)]">
                    <div className={`bg-default-background rounded-[50%] border-solid ${checked ? "border-brand border-2 hover-dark-border" : "border-neutral border hover-neutral-border"} w-6 h-6 absolute left-0 top-0`} />
                    {checked && <div className="bg-brand rounded-[50%] w-3.5 h-3.5 absolute left-[50%] top-[50%] -translate-x-1/2 -translate-y-1/2 hover-dark-bg" />}
                </div>
            </div>
            <div className="flex flex-row gap-0 items-center justify-start shrink-0 relative overflow-hidden" >
                <div className="text-default-text text-left" >
                    {label}
                </div>
            </div>
        </div>
    );
}
