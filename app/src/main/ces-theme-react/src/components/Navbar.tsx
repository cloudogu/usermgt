import {cl} from "dynamic-class-list";
import {useState} from "react";

type NavbarProps = {
    sites: Array<Site>
}

type Site = {
    name: string,
    path: string
}

const logoLightSource = "/src/assets/logo.svg"
const logoDarkSource = "/src/assets/logo_white.svg"

export function Navbar(props?: NavbarProps) {
    const [position, setPosition] = useState(1);
    const [collapse, setCollapse] = useState(true);

    function setPositionToAccount() {
        setPosition(1);
    }

    function setPositionToUsers() {
        setPosition(2);
    }

    function setPositionToGroups() {
        setPosition(3);
    }

    function toogleCollapse() {
        setCollapse(!collapse);
    }

    const pseudoBars = "w-6 h-0.5 bg-primary-font group-hover:bg-primary-font-hover";
    return (
        <nav
            className={cl("flex border-b border-primary-border flex-col sm:flex-row justify-between h-12 p-0 " +
                "box-content font-sans text-black text-xs", (collapse) ? "overflow-hidden" : "")}>
            <ul className={"z-50 flex flex-col sm:flex-row"}>
                <li className={cl("flex h-12 sm:hover:bg-base-hover border-border-default sm:border-b-0 " +
                    "sm:cursor-pointer sm:hover:text-base-font-hover", (collapse) ? "" : "border-b")}>
                    <a href="#/account"
                       onClick={setPositionToAccount}
                       className={"flex h-12 flex-0 hover:bg-base-hover-primary sm:bg-base px-2 group"}>
                        <img src={logoLightSource} alt="Logo"
                             className="group-hover:hidden h-8 my-2 pr-2 justify-center py-2"/>
                        <img src={logoDarkSource} alt="Logo"
                             className="hidden group-hover:block h-8 my-2 pr-2 justify-center py-2"/>
                        <span className={"flex h-12 items-center whitespace-nowrap text-lg"}>
                            User Management
                        </span>
                    </a>
                </li>

                <li>
                    <a href="#/account"
                       onClick={setPositionToAccount}
                       className={"px-2 bg-default flex h-12 items-center whitespace-nowrap hover:bg-base-hover-primary " +
                           "hover:text-base-font-hover" + ((position == 1) ? " bg-base-active" : "")}>Account</a>
                </li>

                <li>
                    <a href="#/users"
                       onClick={setPositionToUsers}
                       className={"px-2 bg-default flex h-12 items-center whitespace-nowrap hover:bg-base-hover-primary " +
                           "hover:text-base-font-hover" + ((position == 2) ? " bg-base-active" : "")}>Users</a>
                </li>

                <li>
                    <a href="#/groups"
                       onClick={setPositionToGroups}
                       className={"px-2 bg-default flex h-12 items-center whitespace-nowrap hover:bg-base-hover-primary hover:text-base-font-hover" +
                           "hover:text-base-font-hover" + ((position == 3) ? " bg-base-active" : "")}>Groups</a>
                </li>
            </ul>
            <ul className={"z-50 border-b border-base-border sm:border-b-0 bg-background flex flex-col sm:flex-row"}>
                <li className="px-2 bg-default flex h-12 items-center whitespace-nowrap cursor-default">
                    <svg className={"h-4 w-4 text-red-500"} width="24" height="24" viewBox="0 0 24 24" stroke-width="2"
                         stroke="currentColor" fill="none" stroke-linecap="round" stroke-linejoin="round">
                        <path stroke="none" d="M0 0h24v24H0z"/>
                        <circle cx="12" cy="7" r="4"/>
                        <path d="M6 21v-2a4 4 0 0 1 4 -4h4a4 4 0 0 1 4 4v2"/>
                    </svg>
                    admin
                </li>
                <li>
                    <a href="#"
                       className="px-2 bg-default flex h-12 items-center whitespace-nowrap hover:bg-base-hover-primary
                       hover:text-base-font-hover">Logout</a>
                </li>
            </ul>

            <button
                className="z-50 sm:hidden absolute right-4 my-1 w-12 space-y-1 bg-primary p-3 rounded-md
                hover:bg-primary-hover group"
                onClick={toogleCollapse}
            >
                <div className={cl(pseudoBars)}></div>
                <div className={cl(pseudoBars)}></div>
                <div className={cl(pseudoBars)}></div>
            </button>
        </nav>
    );
}