import {cl} from "dynamic-class-list";
import {useRef, useState} from "react";
import {Link, useLocation} from "react-router-dom";

const contextPath = process.env.PUBLIC_URL || "/admin";

type NavbarProps = {
    sites: Array<Site>
}

type Site = {
    name: string,
    path: string
}

const logoLightSource = "/src/assets/logo.svg"
const logoDarkSource = "/src/assets/logo_white.svg"

export function Navbar(props: NavbarProps) {
    const [collapse, setCollapse] = useState(true);

    const location = useLocation();
    const administrationLinkRef = useRef<HTMLAnchorElement>(null);
    const loggingLinkRef = useRef<HTMLAnchorElement>(null)


    function toogleCollapse() {
        setCollapse(!collapse);
    }

    function getSelectedClassName(isSelected: boolean): string {
        return isSelected ? "bg-base-active" : "bg-base"
    }

    const pseudoBars = "w-6 h-0.5 bg-primary-font group-hover:bg-primary-font-hover";
    return (
        <nav
            className={cl("w-full flex border-b border-primary-border flex-col sm:flex-row justify-between h-12 p-0 " +
                "box-content font-sans text-black text-xs", (collapse) ? "overflow-hidden" : "")}>
            <ul className={"z-50 flex flex-col sm:flex-row"}>
                <li>
                    <Link
                        className={cl("group flex px-2 h-12 hover:bg-base md:hover:bg-base-hover-primary border-border-default sm:border-b-0 " +
                            "sm:cursor-pointer sm:hover:text-base-font-hover", (collapse) ? "" : "border-b")}
                        ref={administrationLinkRef}
                        to="/account"
                        onClick={(_) => {
                            administrationLinkRef.current?.blur();
                            loggingLinkRef.current?.focus();
                        }}>
                        <img src={logoLightSource} alt="Logo"
                             className="group-hover:hidden h-8 my-2 pr-2 justify-center py-2"/>
                        <img src={logoDarkSource} alt="Logo"
                             className="hidden group-hover:block h-8 my-2 pr-2 justify-center py-2"/>
                        <span className={"flex h-12 items-center whitespace-nowrap text-lg"}>
                            User Management
                        </span>
                    </Link>
                </li>
                {props.sites && props.sites.map((element) => {
                    let selectItem = location.pathname.slice(1) === element.path || (location.pathname === "/" && element.path === "logging");
                    return <li key={element.path}>
                        <Link key={element.path}
                              className={"px-2 flex h-12 items-center whitespace-nowrap hover:bg-base-hover-primary " +
                                  "hover:text-base-font-hover " + (getSelectedClassName(selectItem))}
                              to={element.path}
                              style={{marginLeft: "0px"}}>{element.name}</Link>
                    </li>;
                })}
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
                    <a className="px-2 bg-default flex h-12 items-center whitespace-nowrap hover:bg-base-hover-primary
                       hover:text-base-font-hover" id="logout" href={contextPath + "/api/logout"}>Logout</a>
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