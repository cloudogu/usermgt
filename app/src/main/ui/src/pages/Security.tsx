import {Button, CesIconPlus} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {useLocation, useNavigate} from "react-router-dom";
import {useApplicationContext} from "../components/contexts/ApplicationContext";
import CreatedPATDialog from "../components/security/CreatedPATDialog";
import {PATManagement} from "../components/security/Pat";
import {t} from "../helpers/i18nHelpers";
import {pageTitle} from "../helpers/pageTitle";
import {usePAT} from "../hooks/usePAT";
import type {CreatePATResponse} from "../services/PATs";
import "../ces-styles-wrapper.css";
import "../ces-styles-enforcing.css";
import useDoguSelectionStyles from "../hooks/useDoguSelectionStyles";
import {useSetPageTitle} from "../hooks/useSetPageTitle";

export default function Security() {
    const {casUser} = useApplicationContext();

    return (
        <div className="tailwind-wrapper">
            {!casUser.loading && <SecurityContent username={casUser.principal}/>}
        </div>
    );
}

function SecurityContent({username}: {username: string}) {
    const {pat, isPATLoading, patError} = usePAT(username);
    const navigate = useNavigate();
    const location = useLocation();
    const createdPAT = (location.state as {createdPAT?: CreatePATResponse} | null)?.createdPAT;
    const dialogPAT = createdPAT;
    const closeCreatedPAT = () => navigate("/security", {replace: true, state: null});

    const classes = useDoguSelectionStyles();
    useSetPageTitle(pageTitle("pages.security"));

    return (
        <>
            <div className="flex flex-col gap-4 desktop:flex-row desktop:items-start desktop:justify-between">
                <div className="min-w-0">
                    <h1 className="desktop:text-desktop-6xl mobile:text-mobile-6xl text-brand mb-0">{t("pages.security")}</h1>
                    <h2>{t("security.overview.title")}</h2>
                    <div className="desktop:text-desktop-regular mobile:text-mobile-regular text-neutral flex flex-col">
                        {t("security.overview.title.discription")}
                    </div>
                </div>
                <Button
                    className={`flex w-auto mobile:w-full self-start shrink-0 items-center justify-center gap-1 mt-2 ${classes.focusable}`}
                    color="brand"
                    variant="primary"
                    size="small"
                    data-testid="security-create-pat"
                    onClick={() => navigate("/security/createPAT")}
                >
                    <CesIconPlus/>
                    <span>{t("security.overview.button.createkey")}</span>
                </Button>
            </div>
            <hr className="my-4 border-0 border-t border-neutral-weak" aria-hidden="true"/>
            <section className="pb-8">
                {pat && <PATManagement pat={pat} patError={patError} isPATLoading={isPATLoading}/>}
            </section>
            {dialogPAT && <CreatedPATDialog pat={dialogPAT} onClose={closeCreatedPAT}/>}
        </>
    );
}
