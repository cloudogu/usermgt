import {ApplicationContainer as TailwindContainer, Button, CesIconArrowLeft, CesIconCheck, CesIconSpinner, CesIconTrash, Label} from "@cloudogu/ces-theme-tailwind";
import React, {useState} from "react";
import {Link} from "react-router-dom";
import {useTranslation} from "react-i18next";
import {useNavigate, useParams} from "react-router-dom";
import Breadcrumb from "../components/Breadcrumb";
import StatusIndicator from "../components/StatusIndicator";
import DeletePATDialog from "../components/security/DeletePATDialog";
import {formatDate} from "../helpers/i18nHelpers";
import {pageTitle} from "../helpers/pageTitle";
import {useAPI} from "../hooks/useAPI";
import {useDogus} from "../hooks/useDogus";
import {PATService} from "../services/PATs";
import "../ces-styles-wrapper.css";
import "../ces-styles-enforcing.css";
import {useSetPageTitle} from "../hooks/useSetPageTitle";

export default function PATDetails() {
    const {id} = useParams<{id: string}>();
    const navigate = useNavigate();
    const {t} = useTranslation();
    const {data: tokens, isLoading, error} = useAPI(PATService.getAll);
    const {doguOptions, isLoading: areDogusLoading, error: doguError} = useDogus();
    const pat = tokens?.find(token => token.id === id);
    const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
    const deleteToken = async () => {
        if (!pat) return;
        await PATService.delete(pat.id);
        setDeleteDialogOpen(false);
        navigate("/security", {replace: true});
    };
    const timeHint = pat
        ? `${t("security.overview.table.createdAt")} ${formatDate(pat.createdAt)} · ${pat.expiresAt
            ? `${t("security.overview.table.expiresAt")} ${formatDate(pat.expiresAt)}`
            : t("security.pat.details.neverExpires")}`
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

    useSetPageTitle(pageTitle("pages.patDetails"));
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
                    <div className="flex min-h-[60vh] items-center justify-center">
                        <CesIconSpinner
                            role="status"
                            aria-label={t("pages.patDetails")}
                            className="h-16 w-16 animate-spin"
                        />
                    </div>
                ) : !pat ? (
                    <p role="alert" className="my-4 text-danger">{t("security.pat.details.notFound")}</p>
                ) : (
                    <div>
                        <div className="flex flex-wrap items-center gap-1.5">
                            <h2 className="min-w-0 max-w-full break-words mb-1">
                                {pat.displayName}
                            </h2>
                            <div className="shrink-0">
                                <StatusIndicator
                                    text={
                                        pat.expiresAt && Date.parse(pat.expiresAt) <= Date.now()
                                            ? "expired"
                                            : "active"
                                    }
                                    variant="primary"
                                />
                            </div>
                        </div>
                        <Label text={timeHint} className={"mb-2"}/>
                        <Button
                            className="flex w-auto mobile:w-full items-center justify-center gap-1 mb-2 mt-4"
                            color="neutral"
                            variant="secondary"
                            size="small"
                            type="button"
                            onClick={() => setDeleteDialogOpen(true)}
                        >
                            <CesIconTrash aria-hidden="true"/>
                            <span>{t("security.pat.details.delete")}</span>
                        </Button>
                        <hr className="my-4 border-0 border-t border-neutral-weak" aria-hidden="true" />
                        <h3>{t("security.pat-details.access.label")}</h3>
                        <div className="my-6 break-all">
                            {allDogus ? (
                                <p className="flex items-center gap-2"><CesIconCheck className="h-6 w-6 shrink-0 text-brand" aria-hidden="true"/>{t("security.createpat.scopes.selectdogus.all.label.dogus")} ({t("security.createpat.scopes.selectdogus.all.label.hint")})</p>
                            ) : doguError ? (
                                <p role="alert" className="text-danger">{t("security.pat.details.dogusLoadError")}</p>
                            ) : areDogusLoading ? (
                                    <div className="flex min-h-[60vh] items-center justify-center">
                                        <CesIconSpinner
                                            role="status"
                                            aria-label={t("security.pat.details.scope")}
                                            className="h-16 w-16 animate-spin"
                                        />
                                    </div>
                            ) : (
                                <>
                                    {doguGroups.map(group => (
                                        <section key={group.key} aria-labelledby={`pat-dogus-${group.key}`}
                                            className="pt-4 first:pt-0">
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
                <Link
                    to="/security"
                    className="
                          inline-flex items-center justify-center gap-2
                          h-10 whitespace-nowrap rounded border-2 px-[14px] font-bold
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
                    {t("security.pat.details.back")}
                </Link>
                {deleteDialogOpen && pat && (
                    <DeletePATDialog
                        pat={pat}
                        onClose={() => setDeleteDialogOpen(false)}
                        onConfirm={deleteToken}
                    />
                )}
            </TailwindContainer.ContentContainer.EmptyLargePage>
        </div>
    );
}
