import React from "react";
import {useApplicationContext} from "../components/contexts/ApplicationContext";
import {PTAManagement} from "../components/security/Pat";
import {t} from "../helpers/i18nHelpers";
import {usePAT} from "../hooks/usePAT";

export default function Security() {
    const {casUser} = useApplicationContext();

    return (
        <>
            <h1 className="mt-5 mb-2.5 desktop:text-desktop-6xl mobile:text-mobile-6xl text-brand">{t("pages.security")}</h1>
            {!casUser.loading && <SecurityContent username={casUser.principal}/>}
        </>
    );
}

function SecurityContent({username}: {username: string}) {
    const {pat, isPATLoading, patError} = usePAT(username);

    return (
        <div>
            <h2 className="mt-5 mb-4 desktop:text-desktop-5xl mobile:text-mobile-5xl text-default-text">{t("security.pta.title")}</h2>
            <p className="mb-4 text-neutral">{t("security.pta.title.discription")}</p>
            {pat && <PTAManagement pat={pat} patError={patError} isPATLoading={isPATLoading}/>}
        </div>
    );
}
