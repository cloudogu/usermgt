import {ApplicationContainer as TailwindContainer, Button, CesIconPlus} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import {useApplicationContext} from "../components/contexts/ApplicationContext";
import {PTAManagement} from "../components/security/Pat";
import {t} from "../helpers/i18nHelpers";
import {usePAT} from "../hooks/usePAT";
import {pageTitle} from "../helpers/pageTitle";
import "../styles.css";

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

    return (
        <TailwindContainer.ContentContainer.EmptyLargePage
            applicationTitle={pageTitle("pages.security")}
        >
            <div className="mt-5 mb-2.5 flex items-center justify-between">
                <h1 className="mt-5 mb-2.5 desktop:text-desktop-6xl mobile:text-mobile-6xl text-brand">{t("pages.security")}</h1>
                <Button className="flex items-center justify-center gap-1 rounded-sm border-4 h-6 pl-0.5 pr-2.5 py-1" color="brand" variant="primary">
                    <CesIconPlus/>
                    <span>{t("security.pta.button.createkey")}</span>
                </Button>
            </div>
            <section className="pb-[2rem]">
                {pat && <PTAManagement pat={pat} patError={patError} isPATLoading={isPATLoading}/>}
            </section>
        </TailwindContainer.ContentContainer.EmptyLargePage>
    );
}
