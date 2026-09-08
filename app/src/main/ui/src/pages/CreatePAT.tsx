import {ApplicationContainer as TailwindContainer, Button, InputField, Select, Label} from "@cloudogu/ces-theme-tailwind";
import React, {useState} from "react";
import {createUseStyles} from "react-jss";
import {useNavigate} from "react-router-dom";
import Breadcrumb from "../components/Breadcrumb";
import DoguSelection from "../components/security/DoguSelection"
import {t} from "../helpers/i18nHelpers";
import {pageTitle} from "../helpers/pageTitle";
import "../ces-styles-wrapper.css";


const useStyles = createUseStyles({
    boldLabel: {
        "& label > span:first-of-type": {
            fontWeight: 600,
        },
    },
    dangerLabel: {
        "& label.text-danger > span": {
            color: "#CC3333"
        },
    },
});

export default function CreatePAT() {

    const navigate = useNavigate();
    const [patName, setPatName] = useState<string>("");
    const [selectedDogus, setSelectedDogus] = useState<string[]>([]);
    const classes = useStyles();

    const [touched, setTouched] = useState(false);
    const [, setSelectedOption] = useState<string>("0");

    const nameError =
        patName.trim().length === 0
            ? "Bitte einen Namen eingeben."
            : patName.trim().length < 3
                ? "Der Name muss mindestens 3 Zeichen enthalten."
                : "";

    const showError = touched && nameError !== "";

    return (
        <div className="tailwind-wrapper">
            <TailwindContainer.ContentContainer.EmptyLargePage
                applicationTitle={pageTitle("pages.createPAT")}
            >
                <Breadcrumb
                    items={[
                        [t("pages.security"), "/security"],
                        [t("pages.createPAT")],
                    ]}
                />
                <h1 className="mb-0 desktop:text-desktop-6xl mobile:text-mobile-6xl text-brand">
                    {t("pages.createPAT")}
                </h1>
                <div className={[classes.boldLabel,classes.dangerLabel, "mb-4"].join(" ")}>
                    <InputField type={"text"}
                        variant={showError ? "danger" : undefined}
                        label={t("security.createpat.input.name.label")}
                        hint={t("security.createpat.input.name.hint")}
                        value={patName}
                        required={true}
                        onChange={(e) => {
                            setPatName(e.target.value);
                            setTouched(true);
                        }}
                        onBlur={() => setTouched(true)}
                        aria-invalid={showError}
                        aria-describedby={showError ? "pat-name-error" : undefined}
                        data-testid={"security-create-pat-name-input"}
                    />
                </div>
                <div className={[classes.boldLabel, "mb-4"].join(" ")}>
                    <Label
                        text={t("security.createpat.input.expires.label")}
                    >
                        <Select
                            data-testid={"debug-mode-duration-select"}
                            id={"debug-mode-duration"}
                            onValueChange={setSelectedOption}
                            placeholder={t("security.createpat.select.expires.placeholder")}
                        >
                            <Select.Item value="7" data-testid={"debug-mode-duration-15"}>{t("security.createpat.select.expires.option.sevendays")}</Select.Item>
                            <Select.Item value="30" data-testid={"debug-mode-duration-15"}>{t("security.createpat.select.expires.option.thirtydays")}</Select.Item>
                            <Select.Item value="60" data-testid={"debug-mode-duration-15"}>{t("security.createpat.select.expires.option.sixtydays")}</Select.Item>
                            <Select.Item value="90" data-testid={"debug-mode-duration-15"}>{t("security.createpat.select.expires.option.nintydays")}</Select.Item>
                            <Select.Item value="0" data-testid={"debug-mode-duration-15"}>{t("security.createpat.select.expires.option.never")}</Select.Item>
                        </Select>
                    </Label>
                </div>
                <DoguSelection label={t("security.createpat.scopes.appliedto.label")} value={selectedDogus} onChange={setSelectedDogus} />
                <div className="mt-6 flex flex-row items-end justify-between">
                    <div className="flex flex-row items-center justify-start gap-4">
                        <Button color="brand" variant="primary" size="regular" type="button">
                            Schlüssel anlegen
                        </Button>
                        <Button color="neutral" variant="secondary" size="regular" type="button"
                            onClick={() => navigate("/security")}>
                            Abbrechen
                        </Button>
                    </div>
                    <span className="text-sm text-neutral">* Pflichtfeld</span>
                </div>
            </TailwindContainer.ContentContainer.EmptyLargePage>
        </div>
    );
}
