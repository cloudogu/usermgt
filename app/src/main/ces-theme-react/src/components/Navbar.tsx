import {cl} from "dynamic-class-list";
import {createElement, useReducer, useRef} from "react";
import {Link} from "react-router-dom";
import {ArrowRightOnRectangleIcon} from "@heroicons/react/24/solid";
// @ts-ignore
import lightLogo from '../assets/logo.svg';
// @ts-ignore
import darkLogo from '../assets/logo_white.svg';
import {Bars3Icon, UserIcon} from "@heroicons/react/24/outline";

const contextPath = process.env.PUBLIC_URL || "/admin";

type NavbarProps = {
  sites: Array<Site>;
  currentPath: string;
  toolName: string;
}

export type Site = {
  name: string,
  path: string,
  icon?: any,
}

export function Navbar(props: NavbarProps) {
  const [collapse, toggleCollapse] = useReducer((oldValue) => !oldValue, false);
  const administrationLinkRef = useRef<HTMLAnchorElement>(null);
  const loggingLinkRef = useRef<HTMLAnchorElement>(null);

  return (
    <nav
      className={cl(
        "flex text-nav-primary-font border-b border-nav-primary-border flex-col sm:flex-row justify-between h-12 p-0",
        "box-content font-sans text-xs",
        (collapse) ? "overflow-hidden" : ""
      )}>
      <ul className={"z-50 flex flex-col sm:flex-row"}>
        <li>
          <Link
            className={cl("group flex px-2 h-12 hover:bg-nav-primary-hover text-nav-primary-font hover:text-nav-primary-font-hover sm:border-b-0 " +
              "sm:cursor-pointer sm:hover:text-base-font-hover")}
            ref={administrationLinkRef}
            to="/account"
            onClick={() => {
              administrationLinkRef.current?.blur();
              loggingLinkRef.current?.focus();
            }}>
            <img src={lightLogo} alt="Logo"
                 className="group-hover:hidden h-8 my-2 pr-2 justify-center py-2"/>
            <img src={darkLogo} alt="Logo"
                 className="hidden group-hover:block h-8 my-2 pr-2 justify-center py-2"/>
            <span className={"flex h-12 items-center whitespace-nowrap text-lg"}>
                            {props.toolName}
                        </span>
          </Link>
        </li>
        {props.sites && props.sites.map((element) => {
          return <li key={element.path}>
            <Link key={element.path}
                  className={cl(
                    "px-2 bg-default flex h-12 items-center whitespace-nowrap",
                    "hover:bg-nav-primary-hover hover:text-nav-primary-font-hover",
                    (element.path === props.currentPath) ? "bg-nav-primary-selected" : ""
                  )}
                  to={element.path}
                  style={{marginLeft: "0px"}}>
              {element.icon && createElement(element.icon, {className: "w-5 h-5 sm:hidden mr-2"})}
              {element.name}
            </Link>
          </li>;
        })}
      </ul>

      <ul
        className={"z-50 border-b border-nav-primary-border sm:border-b-0 bg-nav-primary flex flex-col sm:flex-row bg-default"}>
        <li>
          <Link key={"/account"}
                to={"/account"}
                className={cl(
                  "px-2 bg-nav-primary flex h-12 items-center whitespace-nowrap",
                  "hover:text-nav-primary-font-hover hover:bg-nav-primary-hover",
                  ((props.currentPath === "/account") ? "bg-nav-primary-selected" : ""))}>
            <UserIcon className={"w-5 h-5 mr-2"}/>
            <span>Account</span>
          </Link>
        </li>
        <li>
          <a href={contextPath + "/api/logout"} id="logout" className={cl(
            "px-2 bg-nav-primary flex h-12 items-center whitespace-nowrap",
            "hover:text-nav-primary-font-hover hover:bg-nav-primary-hover"
          )}>
            <ArrowRightOnRectangleIcon className={"w-5 h-5 mr-2"}/>
            <span>Logout</span>
          </a>
        </li>
      </ul>

      <button className={cl(
        "sm:hidden w-11 h-9 z-50 rounded-md group",
        "absolute right-4 space-y-1",
        "text-button-primary-font",
        "bg-button-primary hover:bg-button-primary-hover"
      )} onClick={toggleCollapse}>
        <Bars3Icon className={cl("mx-1 mb-1 ")}/>
      </button>
    </nav>
  );
}
