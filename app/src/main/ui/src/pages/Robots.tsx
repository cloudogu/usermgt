import {Button, CesIconPlus} from "@cloudogu/ces-theme-tailwind";
import React, {useEffect, useState} from "react";
import {useLocation, useNavigate} from "react-router-dom";
import {useApplicationContext} from "../components/contexts/ApplicationContext";
import CreatedRobotDialog from "../components/robots/CreatedRobotDialog";
import {RobotManagement} from "../components/robots/RobotManagement";
import {t, tWithParams} from "../helpers/i18nHelpers";
import {pageTitle} from "../helpers/pageTitle";
import {useRobots} from "../hooks/useRobots";
import {useSetPageTitle} from "../hooks/useSetPageTitle";
import type {CreatePATResponse} from "../services/PATs";
import "../ces-styles-wrapper.css";
import "../ces-styles-enforcing.css";


export default function Robots() {
    const {casUser} = useApplicationContext();

    return (
        <div className="tailwind-wrapper">
            {!casUser.loading && <RobotsContent/>}
        </div>
    );
}

function RobotsContent() {
    const {robots, isLoading, error} = useRobots();
    const navigate = useNavigate();
    const location = useLocation();
    const navigationState = location.state as {
        createdPAT?: CreatePATResponse;
        deletedPATName?: string;
    } | null;
    const createdPAT = navigationState?.createdPAT;
    const deletedPATName = navigationState?.deletedPATName;
    const [announcement, setAnnouncement] = useState("");

    useEffect(() => {
        if (deletedPATName !== undefined) {
            setAnnouncement(tWithParams("security.createpat.modal.delete.succeeded", deletedPATName));
            navigate("/robots", {replace: true, state: null});
        }
    }, [deletedPATName, navigate]);
    const dialogPAT = createdPAT;
    const closeCreatedRobot = () => navigate("/robots", {replace: true, state: null});

    useSetPageTitle(pageTitle("pages.robots"));

    return (
        <>
            <div className="flex flex-col gap-4 desktop:flex-row desktop:items-start desktop:justify-between">
                <div className="min-w-0">
                    <h1 className="desktop:text-desktop-6xl mobile:text-mobile-6xl text-brand mb-0">{t("pages.robots")}</h1>
                    <div className="desktop:text-desktop-regular mobile:text-mobile-regular text-neutral flex flex-col">
                        {t("robots.overview.description")}
                    </div>
                </div>
                <Button
                    className="flex w-auto mobile:w-full self-start shrink-0 items-center justify-center gap-1 mt-2"
                    color="brand"
                    variant="primary"
                    size="small"
                    data-testid="robots-create-robot"
                    onClick={() => navigate("/robots/createRobot")}
                >
                    <CesIconPlus aria-hidden="true" />
                    <span>{t("robots.overview.button.create")}</span>
                </Button>
            </div>
            <hr className="my-4 border-0 border-t border-neutral-weak" aria-hidden="true"/>
            <section className="pt-3 pb-8">
                <RobotManagement robots={robots} error={error} isLoading={isLoading}/>
            </section>
            <div role="status" aria-atomic="true" className="sr-only">
                {announcement}
            </div>
            {dialogPAT && <CreatedRobotDialog pat={dialogPAT} onClose={closeCreatedRobot}/>}
        </>
    );
}
