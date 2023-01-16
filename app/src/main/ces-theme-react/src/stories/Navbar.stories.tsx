import {cl} from "dynamic-class-list";

export default {
    title: 'CES Theme/Navigation/Navbar',
};

interface NavbarProps {
    selectUsers?: boolean;
    selectAccount?: boolean;
    collapseInMobile?: boolean;
}

const logoLightSource = "/src/assets/logo.svg"
const logoDarkSource = "/src/assets/logo_white.svg"

export function Navbar(props?: NavbarProps) {
    const selectAccount: boolean = props?.selectAccount || false;
    const selectUsers: boolean = props?.selectUsers || false;
    const collapseInMobile: boolean = props?.collapseInMobile || false;

    const pseudoBars = "w-6 h-0.5 bg-base group-hover:bg-base-hover-primary";
    return (
        <nav
            className={cl("flex border-b border-primary-border flex-col sm:flex-row justify-between h-12 p-0 " +
                "box-content font-sans text-black text-xs", (collapseInMobile) ? "overflow-hidden" : "")}>
            <ul className={"z-50 flex flex-col sm:flex-row"}>
                <li className={cl("flex h-12 sm:hover:bg-base-hover border-border-default sm:border-b-0 " +
                    "sm:cursor-pointer sm:hover:text-base-font-hover", (collapseInMobile) ? "" : "border-b")}>
                    <a href="#" className={"flex h-12 flex-0 hover:bg-base-hover-primary sm:bg-base px-2 group"}>
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
                    <a href="#"
                       className={"px-2 bg-default flex h-12 items-center whitespace-nowrap hover:bg-base-hover-primary " +
                           "hover:text-base-font-hover" + ((selectAccount) ? " bg-base-active" : "")}>Account</a>
                </li>

                <li>
                    <a href="#"
                       className={"px-2 bg-default flex h-12 items-center whitespace-nowrap hover:bg-base-hover-primary " +
                           "hover:text-base-font-hover" + ((selectUsers) ? " bg-base-active" : "")}>Users</a>
                </li>

                <li>
                    <a href="#"
                       className="px-2 bg-default flex h-12 items-center whitespace-nowrap hover:bg-base-hover-primary
                       hover:text-base-font-hover">Groups</a>
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
                hover:bg-primary-hover group">
                <div className={cl(pseudoBars)}></div>
                <div className={cl(pseudoBars)}></div>
                <div className={cl(pseudoBars)}></div>
            </button>
        </nav>
    );
}

export const Default = () => (
    <Navbar/>
);

export const UsersSelected = () => (
    <Navbar selectUsers={true}/>
);

export const CollapsedInMobile = () => (
    <Navbar collapseInMobile={true}/>
);