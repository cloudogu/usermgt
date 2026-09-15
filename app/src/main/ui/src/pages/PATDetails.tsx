import {ApplicationContainer as TailwindContainer, Button, CesIconArrowLeft, CesIconCheck, CesIconSpinner, CesIconTrash, Label} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {useTranslation} from "react-i18next";
import {useNavigate, useParams} from "react-router-dom";
import Breadcrumb from "../components/Breadcrumb";
import StatusIndicator from "../components/StatusIndicator";
import {formatDate} from "../helpers/i18nHelpers";
import {pageTitle} from "../helpers/pageTitle";
import {useAPI} from "../hooks/useAPI";
import {useDogus} from "../hooks/useDogus";
import {PATService} from "../services/PATs";
import "../ces-styles-wrapper.css";
import "../ces-styles-enforcing.css";

export default function PATDetails() {
    const {id} = useParams<{id: string}>();
    const navigate = useNavigate();
    const {t} = useTranslation();
    const {data: tokens, isLoading, error} = useAPI(PATService.getAll);
    const {doguOptions, isLoading: areDogusLoading, error: doguError} = useDogus();
    const pat = tokens?.find(token => token.id === id);
    const timeHint = pat
        ? `${t("security.overview.table.createdAt")} ${formatDate(pat.createdAt)} - ${pat.expiresAt
            ? `${t("security.overview.table.expiresAt")} ${formatDate(pat.expiresAt)}`
            : t("security.pat.details.neverExpires")}`
        : "";
    const scopeParts = new Set((pat?.scope ?? "").split(",").map(part => part.trim()).filter(Boolean));
    const allDogus = scopeParts.has("/*");
    const selectedDogus = doguOptions.filter(dogu => scopeParts.has(dogu.value));
    const doguGroups = [
        {key: "base", dogus: selectedDogus.filter(dogu =>
            !["Administration", "Administration Apps", "Development", "Development Apps"].includes(dogu.category))},
        {key: "development", dogus: selectedDogus.filter(dogu =>
            dogu.category === "Development" || dogu.category === "Development Apps")},
        {key: "administration", dogus: selectedDogus.filter(dogu =>
            dogu.category === "Administration" || dogu.category === "Administration Apps")},
    ].filter(group => group.dogus.length > 0);
    const hasUnknownDogus = !allDogus && [...scopeParts].some(part =>
        !doguOptions.some(dogu => dogu.value === part));


    return (
        <div className="tailwind-wrapper">
            <TailwindContainer.ContentContainer.EmptyLargePage applicationTitle={pageTitle("pages.patDetails")}>
                <Breadcrumb items={[
                    [t("pages.security"), "/security"],
                    [t("pages.patDetails")],
                ]}/>
                <h1 className="desktop:text-desktop-6xl mobile:text-mobile-6xl text-brand break-all">
                    {t("pages.patDetails")}
                </h1>
                {error ? (
                    <p role="alert" className="my-4 text-danger">{t("security.pat.details.loadError")}</p>
                ) : isLoading ? (
                    <CesIconSpinner role="status" aria-label={t("pages.patDetails")}
                        className="h-16 w-16 animate-spin text-divider-primary-border"/>
                ) : !pat ? (
                    <p role="alert" className="my-4 text-danger">{t("security.pat.details.notFound")}</p>
                ) : (
                    <div>
                        <span className="inline-flex items-center gap-1.5">
                            <h2 className={"mb-1"}>{pat.displayName}</h2>
                            <StatusIndicator
                                text={
                                    pat.expiresAt && Date.parse(pat.expiresAt) <= Date.now()
                                        ? "expired"
                                        : "active"
                                }
                                variant="primary"
                            />
                        </span>
                        <Label text={timeHint} className={"mb-2"}/>
                        <Button
                            className="flex items-center justify-center gap-1 mb-2 mt-2"
                            color="neutral"
                            variant="secondary"
                            size="small"
                        >
                            <CesIconTrash/>
                            <span>{t("security.pat.details.delete")}</span>
                        </Button>
                        <hr className="my-4 border-0 border-t border-neutral-300" />
                        <h3>{t("security.pat-details.access.label")}</h3>
                        <div className="my-6 break-all">
                            {allDogus ? (
                                <p>{t("security.createpat.scopes.selectdogus.all.label.dogus")} ({t("security.createpat.scopes.selectdogus.all.label.hint")})</p>
                            ) : doguError ? (
                                <p role="alert" className="text-danger">{t("security.pat.details.dogusLoadError")}</p>
                            ) : areDogusLoading ? (
                                <CesIconSpinner role="status" aria-label={t("security.pat.details.scope")}
                                    className="h-6 w-6 animate-spin"/>
                            ) : (
                                <>
                                    {doguGroups.map(group => (
                                        <section key={group.key} aria-labelledby={`pat-dogus-${group.key}`}
                                            className="border-t border-neutral-weak pt-4 first:border-t-0 first:pt-0">
                                            <span id={`pat-dogus-${group.key}`} className="mb-4 text-default-text font-semibold desktop:text-desktop-regular mobile:text-mobile-regular">
                                                {t(`security.createpat.check.${group.key}`)}
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
                                    {hasUnknownDogus && <p>{t("security.pat.details.unknownDogus")}</p>}
                                    {scopeParts.size === 0 && "—"}
                                </>
                            )}
                        </div>
                    </div>
                )}
                <Button color="brand" variant="primary" type="button"
                    className="flex items-center gap-2" onClick={() => navigate("/security")}>
                    <CesIconArrowLeft aria-hidden="true"/>
                    {t("security.pat.details.back")}
                </Button>
            </TailwindContainer.ContentContainer.EmptyLargePage>
        </div>
    );
}
