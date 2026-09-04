import {ApplicationContainer as TailwindContainer} from "@cloudogu/ces-theme-tailwind";
import React from "react";
import Breadcrumb from "../components/Breadcrumb";
import {t} from "../helpers/i18nHelpers";
import {pageTitle} from "../helpers/pageTitle";
import "../styles.css";


export default function CreatePAT() {
    return (
        <div className="tailwind-wrapper">
            <TailwindContainer.ContentContainer.EmptyLargePage
                applicationTitle={pageTitle("pages.createPAT")}
            >
                <Breadcrumb
                    items={[
                        [t("pages.security"), "/security"],
                        [t("pages.createPAT")],
                    ]}
                />
                <h1 className="mb-0 desktop:text-desktop-6xl mobile:text-mobile-6xl text-brand">
                    {t("pages.createPAT")}
                </h1>
            </TailwindContainer.ContentContainer.EmptyLargePage>
        </div>
    );
}
