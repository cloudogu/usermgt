import {Button, CesIconArrowLeft, CesIconCheck, CesIconSpinner, CesIconTrash, CesIconArrowClockwise, Label, SegmentedSelect} from "@cloudogu/ces-theme-tailwind";
import React, {useId, useRef, useState} from "react";
import {useTranslation} from "react-i18next";
import {createUseStyles} from "react-jss";
import {useNavigate, useParams, Link} from "react-router-dom";
import Breadcrumb from "../components/Breadcrumb";
import StatusIndicator from "../components/StatusIndicator";
import DeleteRobotDialog from "../components/robots/DeleteRobotDialog";
import {formatDate, t} from "../helpers/i18nHelpers";
import {pageTitle} from "../helpers/pageTitle";
import {useAPI} from "../hooks/useAPI";
import {useDogus} from "../hooks/useDogus";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import {RobotsService} from "../services/Robots";
import "../ces-styles-wrapper.css";
import "../ces-styles-enforcing.css";


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

export default function RobotDetails() {
    const {id} = useParams<{id: string}>();
    const [submitAttempted] = useState(false);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const [renewDialogOpen, setRenewDialogOpen] = useState(false);
    const [selectedOption, setSelectedOption] = useState<string>("");

    const navigate = useNavigate();
    const {t} = useTranslation();
    const {data: tokens, isLoading, error} = useAPI(RobotsService.getAll);
    const {doguOptions, isLoading: areDogusLoading, error: doguError} = useDogus();
    const pat = tokens?.find(token => token.id === id);

    const deleteToken = async () => {
        if (!pat) return;
        await RobotsService.delete(pat.id);
        setDeleteDialogOpen(false);
        navigate("/robots", {replace: true, state: {deletedPATName: pat.displayName}});
    };
    const renewToken = async () => {
        if (!pat) return;
        await RobotsService.renew(pat.id);
        setRenewDialogOpen(false);
    };
    const timeHint = pat
        ? `${t("robots.overview.table.createdAt")} ${formatDate(pat.createdAt)}・${pat.expiresAt
            ? `${t("robots.overview.table.expiresAt")} ${formatDate(pat.expiresAt)}`
            : t("robots.pat.details.neverExpires")}`
        : "";
    const scopeParts = new Set((pat?.scope ?? "").split(",").map(part => part.trim()).filter(Boolean));
    const allDogus = scopeParts.has("/*");
    const scopedDoguNames = new Set([...scopeParts].map(scope => scope.replace(/^\//, "")));
    const selectedDogus = doguOptions.filter(dogu => scopedDoguNames.has(dogu.value));
    const doguGroups = [
        {key: "base", dogus: selectedDogus.filter(dogu =>
            !["Administration", "Administration Apps", "Development", "Development Apps"].includes(dogu.category))},
        {key: "development", dogus: selectedDogus.filter(dogu =>
            dogu.category === "Development" || dogu.category === "Development Apps")},
        {key: "administration", dogus: selectedDogus.filter(dogu =>
            dogu.category === "Administration" || dogu.category === "Administration Apps")},
    ].filter(group => group.dogus.length > 0);
    const hasUnknownDogus = !allDogus && [...scopedDoguNames].some(part =>
        !doguOptions.some(dogu => dogu.value === part));

    const isExpired = pat && pat.expiresAt && Date.parse(pat.expiresAt) <= Date.now();
    const classes = useStyles();
    const expiryLabelId = useId();
    const expiryErrorId = useId();
    const expiryError = selectedOption === "" ? t("security.createpat.error.expireat.select") : "";
    const expiryRef = useRef<HTMLButtonElement>(null);
    const showExpiryError = submitAttempted && expiryError !== "";

    useSetPageTitle(pageTitle("pages.robotDetails"));

    return (
        <div className="tailwind-wrapper">
            <Breadcrumb items={[
                [t("pages.robots"), "/robots"],
                [t("pages.robotDetails")],
            ]}/>
            <h1 className="desktop:text-desktop-6xl mobile:text-mobile-6xl text-brand hyphens-auto">
                {t("pages.robotDetails")}
            </h1>
            {error ? (
                <p role="alert" className="my-4 text-danger">{t("robots.pat.details.loadError")}</p>
            ) : isLoading ? (
                <div className="flex min-h-[60vh] items-center justify-center">
                    <CesIconSpinner
                        role="status"
                        aria-label={t("pages.robotDetails")}
                        className="h-16 w-16 animate-spin"
                    />
                </div>
            ) : !pat ? (
                <p role="alert" className="my-4 text-danger">{t("robots.pat.details.notFound")}</p>
            ) : (
                <div className="flex flex-col">
                    <div className="flex flex-wrap items-center gap-4">
                        <h2 className="min-w-0 max-w-full break-words hyphens-auto mb-1 mt-8">
                            {pat.displayName}
                        </h2>
                        <div className="shrink-0 mt-8">
                            <StatusIndicator
                                text={ isExpired ? "expired" : "active" }
                                variant="primary"
                            />
                        </div>
                    </div>
                    <p className="my-2 text-neutral">{timeHint}</p>
                    { isExpired ? (<>
                        <h3>{t("robots.pat.details.manage")}</h3>
                        <h4>{t("robots.pat.details.manage.renewkey")}</h4>
                        <form>
                            <div
                                className={[classes.boldLabel, classes.dangerLabel, "mb-6"].join(" ")}
                            >
                                <Label
                                    text={<span id={expiryLabelId}>{t("robots.pat.details.manage.renewkey.hint")}{requiredStar()}</span>}
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
                                            id="robots-create-pat-expiry"
                                            data-testid="robots-create-pat-expiry-select-trigger"
                                            aria-labelledby={expiryLabelId}
                                            aria-invalid={showExpiryError}
                                            aria-describedby={showExpiryError ? expiryErrorId : undefined}
                                            className={`focus-visible:ces-focused ${showExpiryError ? "border-danger text-default-text" : ""} mobile:w-full`}
                                        >
                                            {t("robots.createrobot.select.expires.placeholder")}
                                        </SegmentedSelect.TriggerButton>
                                        <SegmentedSelect.Content data-testid="robots-create-pat-expiry-select">
                                            <SegmentedSelect.Content.Item value="7" data-testid={"security-create-pat-expiry-7"}>{t("robots.createrobot.select.expires.option.sevendays")}</SegmentedSelect.Content.Item>
                                            <SegmentedSelect.Content.Item value="30" data-testid={"security-create-pat-expiry-30"}>{t("robots.createrobot.select.expires.option.thirtydays")}</SegmentedSelect.Content.Item>
                                            <SegmentedSelect.Content.Item value="60" data-testid={"security-create-pat-expiry-60"}>{t("robots.createrobot.select.expires.option.sixtydays")}</SegmentedSelect.Content.Item>
                                            <SegmentedSelect.Content.Item value="90" data-testid={"security-create-pat-expiry-90"}>{t("robots.createrobot.select.expires.option.nintydays")}</SegmentedSelect.Content.Item>
                                            <SegmentedSelect.Content.Item value="0" data-testid={"security-create-pat-expiry-0"}>{t("robots.createrobot.select.expires.option.never")}</SegmentedSelect.Content.Item>
                                        </SegmentedSelect.Content>
                                    </SegmentedSelect>
                                </Label>
                            </div>
                            <Button
                                className="flex w-full desktop:w-fit items-center justify-center gap-2 mb-2 mt-4 hover:bg-neutral-weaker focus-visible:!bg-transparent"
                                color="brand"
                                variant="primary"
                                size="small"
                                type="button"
                                onClick={() => setDeleteDialogOpen(true)}
                            >
                                <CesIconArrowClockwise aria-hidden="true" weight="bold"/>
                                <span>{t("robots.pat.details.manage.renewkey")}</span>
                            </Button>
                            <h4>{t("robots.pat.details.delete.robot")}</h4>
                            <Button
                                className="flex w-full desktop:w-fit items-center justify-center gap-2 mb-2 mt-4 hover:bg-neutral-weaker focus-visible:!bg-transparent"
                                color="neutral"
                                variant="secondary"
                                size="small"
                                type="button"
                                onClick={() => setDeleteDialogOpen(true)}
                            >
                                <CesIconTrash aria-hidden="true" weight="bold"/>
                                <span>{t("robots.pat.details.delete")}</span>
                            </Button>
                        </form>
                    </>):(<div className={"flex mobile:flex-col mobile:w-full desktop:flex-row items-center gap-4"}>
                        <Button
                            className="flex w-full desktop:w-fit items-center justify-center gap-2 mb-2 mt-4 hover:!bg-warning-weaker focus-visible:!bg-transparent bg-warning"
                            color="brand"
                            variant="primary"
                            size="small"
                            type="button"
                            onClick={() => setDeleteDialogOpen(true)}
                        >
                            <CesIconArrowClockwise aria-hidden="true" weight="bold"/>
                            <span>{t("robots.pat.details.manage.renewkey")}</span>
                        </Button>
                        <Button
                            className="flex w-full desktop:w-fit items-center justify-center gap-2 mb-2 mt-4 hover:bg-neutral-weaker focus-visible:!bg-transparent"
                            color="neutral"
                            variant="secondary"
                            size="small"
                            type="button"
                            onClick={() => setDeleteDialogOpen(true)}
                        >
                            <CesIconTrash aria-hidden="true" weight="bold"/>
                            <span>{t("robots.pat.details.delete")}</span>
                        </Button>
                    </div>)}

                    <hr className="my-4 border-0 border-t border-neutral-weak" aria-hidden="true" />
                    <h3 className="mt-4">{t("robots.pat.details.access.label")}</h3>
                    <div className="my-4 break-all">
                        {allDogus ? (
                            <p className="flex items-center gap-2"><CesIconCheck className="h-6 w-6 shrink-0 text-brand" aria-hidden="true"/>{t("robots.createrobot.scopes.selectdogus.all.label.dogus")} ({t("robots.createrobot.scopes.selectdogus.all.label.hint")})</p>
                        ) : doguError ? (
                            <p role="alert" className="text-danger">{t("robots.pat.details.dogusLoadError")}</p>
                        ) : areDogusLoading ? (
                            <div className="flex min-h-[60vh] items-center justify-center">
                                <CesIconSpinner
                                    role="status"
                                    aria-label={t("robots.pat.details.scope")}
                                    className="h-16 w-16 animate-spin"
                                />
                            </div>
                        ) : (
                            <>
                                {doguGroups.map(group => (
                                    <section key={group.key} aria-labelledby={`pat-dogus-${group.key}`}
                                        className="pt-4 first:pt-0">
                                        <span id={`pat-dogus-${group.key}`} className="mb-4 text-default-text font-semibold desktop:text-desktop-regular mobile:text-mobile-regular">
                                            {t(`robots.createrobot.check.${group.key}`)}
                                        </span>
                                        <ul className="mb-4 mt-2 flex list-none flex-col gap-2 p-0">
                                            {group.dogus.map(dogu => (
                                                <li key={dogu.value} className="flex items-center gap-2 text-default-text">
                                                    <CesIconCheck className="h-6 w-6 shrink-0 text-brand" aria-hidden="true"/>
                                                    <span>{dogu.label}</span>
                                                </li>
                                            ))}
                                        </ul>
                                    </section>
                                ))}
                                {hasUnknownDogus && <p>{t("robots.pat.details.unknownDogus")}</p>}
                                {scopeParts.size === 0 && "—"}
                            </>
                        )}
                    </div>
                </div>
            )}
            <Link
                to="/robots"
                className="
                      inline-flex items-center justify-center gap-2
                      h-10 whitespace-nowrap rounded border-2 px-3.5 font-bold
                      desktop:text-desktop-regular mobile:text-mobile-regular
                      bg-brand border-brand
                      hover:bg-brand-strong hover:border-brand-strong
                      focus-visible:bg-brand-strong focus-visible:border-brand-strong
                      active:bg-brand-stronger active:border-brand-stronger
                      !text-inverted-text !no-underline
                      outline-0 focus-visible:ces-focused
                      w-auto mobile:w-full
                      mt-6
                "
            >
                <CesIconArrowLeft aria-hidden="true"/>
                {t("robots.pat.details.back")}
            </Link>
            {deleteDialogOpen && pat && (
                <DeleteRobotDialog
                    pat={pat}
                    onClose={() => setDeleteDialogOpen(false)}
                    onConfirm={deleteToken}
                />
            )}
            {renewDialogOpen && pat && (
                <DeleteRobotDialog
                    pat={pat}
                    onClose={() => setDeleteDialogOpen(false)}
                    onConfirm={renewToken}
                />
            )}
        </div>
    );
}
