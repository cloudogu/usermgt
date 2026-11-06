import {Button, InputField, SegmentedSelect, Label, CesIconSpinner} from "@cloudogu/ces-theme-tailwind";
import {isAxiosError} from "axios";
import React, {useId, useRef, useState} from "react";
import {flushSync} from "react-dom";
import {useNavigate} from "react-router-dom";
import Breadcrumb from "../components/Breadcrumb";
import DoguSelection from "../components/security/DoguSelection";
import {t} from "../helpers/i18nHelpers";
import {pageTitle} from "../helpers/pageTitle";
import {requiredStar} from "../helpers/snippets";
import {useAPI} from "../hooks/useAPI";
import useDoguSelectionStyles from "../hooks/useDoguSelectionStyles";
import usePATFormStyles from "../hooks/usePATFormStyles";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import {RobotsService} from "../services/Robots";
import type {PATMetadata} from "../services/PATs";
import "../ces-styles-wrapper.css";


export default function CreateRobot() {
    const {data: tokens, isLoading, error} = useAPI(RobotsService.getAll);

    if (error) {
        return <p role="alert" className="my-4 text-danger">{t("security.pta.load.error")}</p>;
    }
    if (isLoading || !tokens) {
        return  <div className="flex min-h-[60vh] items-center justify-center">
            <CesIconSpinner
                role="status"
                aria-label={t("pages.createRobot")}
                className="h-16 w-16 animate-spin"
            />
        </div>;
    }

    return <CreateRobotForm tokens={tokens}/>;
}

export function CreateRobotForm({tokens}: {tokens: Pick<PATMetadata, "displayName">[]}) {

    const navigate = useNavigate();
    const [robotName, setRobotName] = useState<string>("");
    const [conflictingName, setConflictingName] = useState<string>();
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [createError, setCreateError] = useState(false);
    const [selectedDogus, setSelectedDogus] = useState<string[]>([]);
    const classes = usePATFormStyles();

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

    const createRobot = async () => {
        if (isSubmitting) return;
        setCreateError(false);
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
            const radio = group?.querySelector<HTMLInputElement>("input[type=\"radio\"]:checked")
                ?? group?.querySelector<HTMLInputElement>("input[type=\"radio\"]");
            radio?.focus();
            return;
        }
        const expiresInDays = Number(selectedOption);
        const expiresAt = expiresInDays > 0
            ? new Date(new Date().setDate(new Date().getDate() + expiresInDays)).toISOString()
            : undefined;
        setIsSubmitting(true);
        try {
            const response = await RobotsService.create({
                displayName: robotName,
                expiresAt,
                scope: selectedDogus.includes("/*") ? "/*" : selectedDogus.map(dogu => `/${dogu}`).join(","),
            });
            navigate("/robots", {state: {createdPAT: response}});
        } catch (error) {
            if (isAxiosError(error) && error.response?.status === 409) {
                flushSync(() => setConflictingName(robotName));
                if (nameRef.current?.value === robotName) {
                    nameRef.current.focus();
                }
            } else {
                setCreateError(true);
            }
        } finally {
            setIsSubmitting(false);
        }
    };

    const nameError =
        robotName.trim().length === 0
            ? t("security.createpat.error.displayname.empty")
            : robotName.length < 2
                ? t("robots.createrobot.error.displayname.minlength")
                : robotName.length > 64
                    ? t("security.createpat.error.displayname.length")
                    : /[^a-zA-Z0-9_@.-]/.test(robotName)
                        ? t("robots.createrobot.error.displayname.invalidchars")
                        : conflictingName === robotName || tokens.some(token => token.displayName === robotName)
                            ? t("robots.createrobot.error.displayname.exists")
                            : "";

    const showError = touched && nameError !== "";
    const expiryError = selectedOption === "" ? t("security.createpat.error.expireat.select") : "";
    const showExpiryError = submitAttempted && expiryError !== "";
    useSetPageTitle(pageTitle("pages.createRobot"));
    const defclasses = useDoguSelectionStyles();
    return (
        <div className="tailwind-wrapper">
            <Breadcrumb
                items={[
                    [t("pages.robots"), "/robots"],
                    [t("pages.createRobot")],
                ]}
            />
            <h1 className="mb-4 desktop:text-desktop-6xl mobile:text-mobile-6xl text-brand hyphens-auto">
                {t("pages.createRobot")}
            </h1>
            <form>
                <div className={[classes.boldLabel,classes.dangerLabel,classes.defaultTextLabel, "mb-6 mt-12"].join(" ")}>
                    <InputField type={"text"}
                        ref={nameRef}
                        aria-labelledby={nameLabelId}
                        aria-describedby={nameHintId}
                        variant={showError ? "danger" : undefined}
                        label={<span id={nameLabelId}>{t("robots.createrobot.input.name.label")}{requiredStar()}</span>}
                        hint={<span id={nameHintId}>{showError ? nameError : t("robots.createrobot.input.name.hint")}</span>}
                        value={robotName}
                        required={true}
                        onChange={(e) => {
                            setRobotName(e.target.value);
                            setConflictingName(undefined);
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
                        text={<span id={expiryLabelId}>{t("robots.createrobot.input.expires.label")}{requiredStar()}</span>}
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
                                {t("robots.createrobot.select.expires.placeholder")}
                            </SegmentedSelect.TriggerButton>
                            <SegmentedSelect.Content data-testid="security-create-pat-expiry-select">
                                <SegmentedSelect.Content.Item value="7" data-testid={"security-create-pat-expiry-7"}>{t("robots.createrobot.select.expires.option.sevendays")}</SegmentedSelect.Content.Item>
                                <SegmentedSelect.Content.Item value="30" data-testid={"security-create-pat-expiry-30"}>{t("robots.createrobot.select.expires.option.thirtydays")}</SegmentedSelect.Content.Item>
                                <SegmentedSelect.Content.Item value="60" data-testid={"security-create-pat-expiry-60"}>{t("robots.createrobot.select.expires.option.sixtydays")}</SegmentedSelect.Content.Item>
                                <SegmentedSelect.Content.Item value="90" data-testid={"security-create-pat-expiry-90"}>{t("robots.createrobot.select.expires.option.nintydays")}</SegmentedSelect.Content.Item>
                                <SegmentedSelect.Content.Item value="0" data-testid={"security-create-pat-expiry-0"}>{t("robots.createrobot.select.expires.option.never")}</SegmentedSelect.Content.Item>
                            </SegmentedSelect.Content>
                        </SegmentedSelect>
                    </Label>
                </div>
                <div ref={dogusRef}>
                    <DoguSelection
                        label={<>{t("robots.createrobot.scopes.appliedto.label")}{requiredStar()}</>}
                        value={selectedDogus}
                        onChange={setSelectedDogus}
                        invalid={submitAttempted && selectedDogus.length === 0}
                        showUnsupportedDogus={true}
                    />
                </div>
                {createError && (
                    <p role="alert" className="my-4 text-danger">{t("robots.createrobot.error.create")}</p>
                )}
                <div className="mt-6 flex mobile:flex-col desktop:flex-row gap-4 items-end justify-between mb-4">
                    <div className="flex mobile:flex-col mobile:w-full desktop:flex-row items-center gap-4">
                        <Button color="brand" variant="primary" size="regular" type="button" data-testid="security-create-pat-submit"
                            onClick={createRobot}
                            disabled={isSubmitting}
                            className={`${defclasses.checkboxFocus} mobile:w-full`}>
                            {t("robots.createrobot.selectdogus.createkey")}
                        </Button>
                        <Button color="neutral" variant="secondary" size="regular" type="button"
                            onClick={() => navigate("/robots")}
                            className={"mobile:w-full"}>
                            {t("robots.createrobot.selectdogus.cancel")}
                        </Button>
                    </div>
                    <div aria-hidden="true" className="text-sm text-neutral mobile:order-first mobile:self-end mobile:my-4">{t("robots.createrobot.legend")}</div>
                </div>
            </form>
        </div>
    );
}
