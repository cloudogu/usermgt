import {ApplicationContainer as TailwindContainer, Button, CesIconPlus} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {useNavigate} from "react-router-dom";
import {useApplicationContext} from "../components/contexts/ApplicationContext";
import {PTAManagement} from "../components/security/Pat";
import {t} from "../helpers/i18nHelpers";
import {pageTitle} from "../helpers/pageTitle";
import {usePAT} from "../hooks/usePAT";
import "../ces-styles-wrapper.css";
import "../ces-styles-enforcing.css";

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

    return (
        <TailwindContainer.ContentContainer.EmptyLargePage
            applicationTitle={pageTitle("pages.security")}
        >
            <div className="flex items-baseline items- justify-between">
                <h1 className="desktop:text-desktop-6xl mobile:text-mobile-6xl text-brand mb-0">{t("pages.security")}</h1>
                <Button
                    className="flex items-center justify-center gap-1"
                    color="brand"
                    variant="primary"
                    size="small"
                    onClick={() => navigate("/security/createPAT")}
                >
                    <CesIconPlus/>
                    <span>{t("security.overview.button.createkey")}</span>
                </Button>
            </div>
            <section className="pb-[2rem]">
                {pat && <PTAManagement pat={pat} patError={patError} isPATLoading={isPATLoading}/>}
            </section>
        </TailwindContainer.ContentContainer.EmptyLargePage>
    );
}
