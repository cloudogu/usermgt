import {CheckboxField} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {t} from "../../helpers/i18nHelpers";
import useDoguSelectionStyles from "../../hooks/useDoguSelectionStyles";
import {useDogus} from "../../hooks/useDogus";
import {CheckBoxGroup} from "./CheckBoxGroup";
import {RadioGroup} from "./RadioGroup";
import {RadioGroupEntry} from "./RadioGroupEntry";
import type {DoguOption} from "../../services/Dogus";

export type DoguSelectionProps = {
    label: React.ReactNode;
    value: string[];
    onChange: (_value: string[]) => void;
    invalid?: boolean;
};

export function DoguSelection({label, value, onChange, invalid = false}: DoguSelectionProps) {
    const classes = useDoguSelectionStyles();
    const {doguOptions} = useDogus();
    const administrationDogus: DoguOption[] = [];
    const developmentDogus: DoguOption[] = [];
    const basicDogus: DoguOption[] = [];
    const excludedDogus: DoguOption[] = [];
    doguOptions.forEach(dogu => {
        if (!dogu.tags.includes("pat")){
            excludedDogus.push(dogu);
        } else if (dogu.category === "Administration" || dogu.category === "Administration Apps") {
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

    const doguCount = doguOptions.length - excludedDogus.length

    return (
        <RadioGroup label={label} className="font-semibold" invalid={invalid}>
            <RadioGroupEntry testId="security-pat-selected-dogus" label="Auswahl an Dogus" checked={!allDogus}
                onChange={() => onChange([])} selectedCount={selectedDogus.length} totalCount={doguCount} onClear={() => onChange([])}>
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
