import {Button, InputField, SegmentedSelect, Label, CesIconSpinner} from "@cloudogu/ces-theme-tailwind";
import React, {useId, useRef, useState} from "react";
import {flushSync} from "react-dom";
import {createUseStyles} from "react-jss";
import {useNavigate} from "react-router-dom";
import Breadcrumb from "../components/Breadcrumb";
import DoguSelection from "../components/security/DoguSelection";
import {t} from "../helpers/i18nHelpers";
import {pageTitle} from "../helpers/pageTitle";
import {useAPI} from "../hooks/useAPI";
import useDoguSelectionStyles from "../hooks/useDoguSelectionStyles";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import {PATService} from "../services/PATs";
import type {PATMetadata} from "../services/PATs";
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
    defaultTextLabel: {
        "& label > span": {
            color: "#0D1C26",
        },
    },
});


function requiredStar() {
    return <span aria-label={t("components.required.asterisk.hint")}>*</span>;
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
        </div>;
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
    const [submitAttempted, setSubmitAttempted] = useState(false);
    const nameRef = useRef<HTMLInputElement>(null);
    const expiryRef = useRef<HTMLButtonElement>(null);
    const dogusRef = useRef<HTMLDivElement>(null);
    const nameLabelId = useId();
    const nameHintId = useId();
    const expiryLabelId = useId();
    const expiryErrorId = useId();

    const createPAT = async () => {
        // Render the field descriptions before moving focus, including on repeated attempts.
        flushSync(() => {
            setTouched(true);
            setSubmitAttempted(true);
        });
        if (nameError) {
            nameRef.current?.focus();
            return;
        }
        if (expiryError) {
            expiryRef.current?.focus();
            return;
        }
        if (selectedDogus.length === 0) {
            const group = dogusRef.current;
            const radio = group?.querySelector<HTMLElement>("[role=\"radio\"][aria-checked=\"true\"]")
                ?? group?.querySelector<HTMLElement>("[role=\"radio\"]");
            radio?.focus();
            return;
        }
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
    const showExpiryError = submitAttempted && expiryError !== "";
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
            <h1 className="mb-4 desktop:text-desktop-6xl mobile:text-mobile-6xl text-brand">
                {t("pages.createPAT")}
            </h1>
            <form>
                <div className={[classes.boldLabel,classes.dangerLabel,classes.defaultTextLabel, "mb-6"].join(" ")}>
                    <InputField type={"text"}
                        ref={nameRef}
                        aria-labelledby={nameLabelId}
                        aria-describedby={nameHintId}
                        variant={showError ? "danger" : undefined}
                        label={<span id={nameLabelId}>{t("security.createpat.input.name.label")}{requiredStar()}</span>}
                        hint={<span id={nameHintId}>{showError ? nameError : t("security.createpat.input.name.hint")}</span>}
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
                    className={[classes.boldLabel, classes.dangerLabel, "mb-6"].join(" ")}
                >
                    <Label
                        text={<span id={expiryLabelId}>{t("security.createpat.input.expires.label")}{requiredStar()}</span>}
                        variant={showExpiryError ? "danger" : undefined}
                        hint={showExpiryError ? <span id={expiryErrorId}>{expiryError}</span> : undefined}
                        className={["desktop:text-desktop-regular", "mobile:text-mobile-regular", showExpiryError ? "text-danger": "text-default-text"].join(" ")}
                    >
                        <SegmentedSelect
                            value={selectedOption}
                            required={true}
                            onValueChange={setSelectedOption}
                        >
                            <SegmentedSelect.TriggerButton
                                ref={expiryRef}
                                id="security-create-pat-expiry"
                                data-testid="security-create-pat-expiry-select-trigger"
                                aria-labelledby={expiryLabelId}
                                aria-invalid={showExpiryError}
                                aria-describedby={showExpiryError ? expiryErrorId : undefined}
                                className={`focus-visible:ces-focused ${showExpiryError ? "border-danger text-default-text" : ""} mobile:w-full`}
                            >
                                {t("security.createpat.select.expires.placeholder")}
                            </SegmentedSelect.TriggerButton>
                            <SegmentedSelect.Content data-testid="security-create-pat-expiry-select">
                                <SegmentedSelect.Content.Item value="7" data-testid={"security-create-pat-expiry-7"}>{t("security.createpat.select.expires.option.sevendays")}</SegmentedSelect.Content.Item>
                                <SegmentedSelect.Content.Item value="30" data-testid={"security-create-pat-expiry-30"}>{t("security.createpat.select.expires.option.thirtydays")}</SegmentedSelect.Content.Item>
                                <SegmentedSelect.Content.Item value="60" data-testid={"security-create-pat-expiry-60"}>{t("security.createpat.select.expires.option.sixtydays")}</SegmentedSelect.Content.Item>
                                <SegmentedSelect.Content.Item value="90" data-testid={"security-create-pat-expiry-90"}>{t("security.createpat.select.expires.option.nintydays")}</SegmentedSelect.Content.Item>
                                <SegmentedSelect.Content.Item value="0" data-testid={"security-create-pat-expiry-0"}>{t("security.createpat.select.expires.option.never")}</SegmentedSelect.Content.Item>
                            </SegmentedSelect.Content>
                        </SegmentedSelect>
                    </Label>
                </div>
                <div ref={dogusRef}>
                    <DoguSelection
                        label={<>{t("security.createpat.scopes.appliedto.label")}{requiredStar()}</>}
                        value={selectedDogus}
                        onChange={setSelectedDogus}
                        invalid={submitAttempted && selectedDogus.length === 0}
                    />
                </div>
                <div className="mt-6 flex mobile:flex-col desktop:flex-row gap-4 items-end justify-between mb-4">
                    <div className="flex mobile:flex-col mobile:w-full desktop:flex-row items-center gap-4">
                        <Button color="brand" variant="primary" size="regular" type="button" data-testid="security-create-pat-submit"
                                onClick={createPAT}
                                className={`${defclasses.checkboxFocus} mobile:w-full`}>
                            {t("security.createpat.selectdogus.createkey")}
                        </Button>
                        <Button color="neutral" variant="secondary" size="regular" type="button"
                                onClick={() => navigate("/security")}
                                className={`mobile:w-full`}>
                            {t("security.createpat.selectdogus.cancel")}
                        </Button>
                    </div>
                    <span className="text-sm text-neutral mobile:order-first mobile:self-end mobile:my-4">* Pflichtfeld</span>
                </div>
            </form>
        </div>
    );
}
