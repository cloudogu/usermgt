import {Button, InputField, Select, Label, CesIconSpinner} from "@cloudogu/ces-theme-tailwind";
import React, {useRef, useState} from "react";
import {createUseStyles} from "react-jss";
import {useNavigate} from "react-router-dom";
import Breadcrumb from "../components/Breadcrumb";
import DoguSelection from "../components/security/DoguSelection";
import {t} from "../helpers/i18nHelpers";
import {pageTitle} from "../helpers/pageTitle";
import {useAPI} from "../hooks/useAPI";
import {PATService} from "../services/PATs";
import type {PATMetadata} from "../services/PATs";
import "../ces-styles-wrapper.css";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import useDoguSelectionStyles from "../hooks/useDoguSelectionStyles";


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
    defaultTextLabel: {
        "& label > span": {
            color: "#0D1C26",
        },
    },
});


function requiredStar() {
    return <span aria-label={t("components.required.asterisk.hint")}>*</span>
}

export default function CreatePAT() {
    const {data: tokens, isLoading, error} = useAPI(PATService.getAll);

    if (error) {
        return <p role="alert" className="my-4 text-danger">{t("security.pta.load.error")}</p>;
    }
    if (isLoading || !tokens) {
        return  <div className="flex min-h-[60vh] items-center justify-center">
                    <CesIconSpinner
                        role="status"
                        aria-label={t("pages.createPAT")}
                        className="h-16 w-16 animate-spin"
                    />
                </div>
    }

    return <CreatePATForm tokens={tokens}/>;
}

export function CreatePATForm({tokens}: {tokens: Pick<PATMetadata, "displayName">[]}) {

    const navigate = useNavigate();
    const [patName, setPatName] = useState<string>("");
    const [selectedDogus, setSelectedDogus] = useState<string[]>([]);
    const classes = useStyles();

    const [touched, setTouched] = useState(false);
    const [selectedOption, setSelectedOption] = useState<string>("");
    const [expiryTouched, setExpiryTouched] = useState(false);
    const [dogusTouched, setDogusTouched] = useState(false);
    const expiryOpen = useRef(false);

    const createPAT = async () => {
        setTouched(true);
        setExpiryTouched(true);
        setDogusTouched(true);
        if (nameError || expiryError || selectedDogus.length === 0) return;
        const expiresInDays = Number(selectedOption);
        const expiresAt = expiresInDays > 0
            ? new Date(Date.now() + expiresInDays * 24 * 60 * 60 * 1000).toISOString()
            : undefined;
        const response = await PATService.create({
            displayName: patName,
            expiresAt,
            scope: selectedDogus.includes("/*") ? "/*" : selectedDogus.map(dogu => `/${dogu}`).join(","),
        });
        navigate("/security", {state: {createdPAT: response}});
    };

    const nameError =
        patName.trim().length === 0
            ? t("security.createpat.error.displayname.empty")
            : patName.length > 64
                ? t("security.createpat.error.displayname.length")
                : /[\s\p{C}\p{Z}\p{Default_Ignorable_Code_Point}]/u.test(patName)
                    ? t("security.createpat.error.displayname.invalidchars")
                    : tokens.some(token => token.displayName === patName)
                        ? t("security.createpat.error.displayname.exists")
                        : "";

    const showError = touched && nameError !== "";
    const expiryError = selectedOption === "" ? t("security.createpat.error.expireat.select") : "";
    const showExpiryError = expiryTouched && expiryError !== "";
    useSetPageTitle(pageTitle("pages.createPAT"));
    const defclasses = useDoguSelectionStyles();
    return (
        <div className="tailwind-wrapper">
            <Breadcrumb
                items={[
                    [t("pages.security"), "/security"],
                    [t("pages.createPAT")],
                ]}
            />
            <h1 className="mb-0 desktop:text-desktop-6xl mobile:text-mobile-6xl text-brand">
                {t("pages.createPAT")}
            </h1>
            <form>
                <div className={[classes.boldLabel,classes.dangerLabel,classes.defaultTextLabel, "mb-4"].join(" ")}>
                    <InputField type={"text"}
                        variant={showError ? "danger" : undefined}
                                label={<>{t("security.createpat.input.name.label")}{requiredStar()}</>}
                        hint={showError ? nameError : t("security.createpat.input.name.hint")}
                        value={patName}
                        required={true}
                        onChange={(e) => {
                            setPatName(e.target.value);
                            setTouched(true);
                        }}
                        className={`${classes.defaultTextLabel} focus-visible:ces-focused`}
                        onBlur={() => setTouched(true)}
                        aria-invalid={showError}
                        data-testid={"security-create-pat-name-input"}
                    />
                </div>
                <div
                    className={[classes.boldLabel, classes.dangerLabel, "mb-4"].join(" ")}
                    onBlur={(event) => {
                        if (!expiryOpen.current && !event.currentTarget.contains(event.relatedTarget)) {
                            setExpiryTouched(true);
                        }
                    }}
                >
                    <Label
                        text={<>{t("security.createpat.input.expires.label")}{requiredStar()}</>}
                        variant={showExpiryError ? "danger" : undefined}
                        hint={showExpiryError ? expiryError : undefined}
                        className={["desktop:text-desktop-regular", "mobile:text-mobile-regular", showExpiryError ? "text-danger": "text-default-text"].join(" ")}
                    >
                        <Select
                            data-testid={"security-create-pat-expiry-select"}
                            id={"security-create-pat-expiry"}
                            value={selectedOption}
                            required={true}
                            onOpenChange={(open) => { expiryOpen.current = open; }}
                            onValueChange={setSelectedOption}
                            placeholder={t("security.createpat.select.expires.placeholder")}
                            className={`focus-visible:ces-focused ${showExpiryError ? "border-danger text-default-text" : ""}`}
                        >
                            <Select.Item value="7" data-testid={"security-create-pat-expiry-7"}>{t("security.createpat.select.expires.option.sevendays")}</Select.Item>
                            <Select.Item value="30" data-testid={"security-create-pat-expiry-30"}>{t("security.createpat.select.expires.option.thirtydays")}</Select.Item>
                            <Select.Item value="60" data-testid={"security-create-pat-expiry-60"}>{t("security.createpat.select.expires.option.sixtydays")}</Select.Item>
                            <Select.Item value="90" data-testid={"security-create-pat-expiry-90"}>{t("security.createpat.select.expires.option.nintydays")}</Select.Item>
                            <Select.Item value="0" data-testid={"security-create-pat-expiry-0"}>{t("security.createpat.select.expires.option.never")}</Select.Item>
                        </Select>
                    </Label>
                </div>
                <div onBlur={(event) => {
                    if (!event.currentTarget.contains(event.relatedTarget)) {
                        setDogusTouched(true);
                    }
                }}>
                    <DoguSelection
                        label={<>{t("security.createpat.scopes.appliedto.label")}{requiredStar()}</>}
                        value={selectedDogus}
                        onChange={setSelectedDogus}
                        invalid={dogusTouched && selectedDogus.length === 0}
                    />
                </div>
                <div className="mt-6 flex flex-row items-end justify-between">
                    <div className="flex flex-row items-center justify-start gap-4">
                        <Button color="brand" variant="primary" size="regular" type="button" data-testid="security-create-pat-submit" onClick={createPAT} className={defclasses.checkboxFocus}>
                            {t("security.createpat.selectdogus.createkey")}
                        </Button>
                        <Button color="neutral" variant="secondary" size="regular" type="button"
                            onClick={() => navigate("/security")}>
                            {t("security.createpat.selectdogus.cancel")}
                        </Button>
                    </div>
                    <span className="text-sm text-neutral">* Pflichtfeld</span>
                </div>
            </form>
        </div>
    );
}
