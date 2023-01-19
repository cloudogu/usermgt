import {cl} from "dynamic-class-list";
import {useReducer, useRef} from "react";
import {Link} from "react-router-dom";
// @ts-ignore
import lightLogo from '../assets/logo.svg';
// @ts-ignore
import darkLogo from '../assets/logo_white.svg';
import {Bars3Icon} from "@heroicons/react/24/outline";
import NavIcon from "./NavIcon";

type NavbarProps = {
  sites: Array<Site>;
  currentPath: string;
  toolName: string;
  loggedInUser: User;
  logoutUri: string;
};

export type Site = {
  name: string,
  path: string,
  icon?: any,
};

export type User = {
  name: string,
};

export default function Navbar(props: NavbarProps) {
  const [collapsed, toggleCollapse] = useReducer((oldValue) => !oldValue, false);
  const administrationLinkRef = useRef<HTMLAnchorElement>(null);
  const loggingLinkRef = useRef<HTMLAnchorElement>(null);

  return (
    <nav
      className={cl(
        "flex text-nav-primary-font border-nav-primary-border flex-col sm:flex-row justify-between h-12 p-0",
        "box-content font-sans text-xs border-b",
        (collapsed) ? "overflow-hidden" : ""
      )}>
      <ul className={"z-50 flex flex-col sm:flex-row sm:ml-auto 2xl:w-[1000px] lg:w-[800px] md:w-[580px] sm:w-[440px]"}>
        <li
          className={cl("flex h-12 border-nav-primary-border sm:border-b-0 sm:cursor-pointer justify-between", (collapsed) ? "" : "border-b")}>
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
          <div className={"flex items-center pr-1"}>
            <button className={cl(
              "sm:hidden w-11 h-9 z-50 rounded-md group",
              "space-y-1 flex-0 space-between",
              "text-button-primary-font",
              "bg-button-primary hover:bg-button-primary-hover"
            )} onClick={toggleCollapse}>
              <Bars3Icon className={cl("mx-1 mb-1")}/>
            </button>
          </div>
        </li>
        {props.sites && props.sites.map((element) => {
          return <li key={element.path}>
            <Link key={element.path}
                  className={cl(
                    "px-2 bg-default flex h-12 items-center whitespace-nowrap",
                    "hover:bg-nav-primary-hover hover:text-nav-primary-font-hover",
                    (element.path === props.currentPath) ? "bg-nav-primary-selected" : ""
                  )}
                  to={element.path}>
              <NavIcon type={element.icon} className={"sm:hidden"}/>
              {element.name}
            </Link>
          </li>;
        })}
      </ul>
      <ul
        className={"z-50 border-b border-nav-primary-border sm:border-b-0 bg-nav-primary flex flex-col sm:flex-row bg-default sm:mr-auto sm:min-w-[170px]"}>
        <li>
          <Link key={"/account"}
                to={"/account"}
                className={cl(
                  "px-2 flex h-12 items-center whitespace-nowrap",
                  "hover:text-nav-primary-font-hover hover:bg-nav-primary-hover",
                  ((props.currentPath === "/account") ? "bg-nav-primary-selected" : ""))}>
            <NavIcon type={"user"}/>
            <span>{props?.loggedInUser?.name}</span>
          </Link>
        </li>
        <li>
          <a href={props.logoutUri} id="logout" className={cl(
            "px-2 bg-nav-primary flex h-12 items-center whitespace-nowrap",
            "hover:text-nav-primary-font-hover hover:bg-nav-primary-hover"
          )}>
            <NavIcon type={"logout"}/>
            <span>Logout</span>
          </a>
        </li>
      </ul>
    </nav>
  );
}
