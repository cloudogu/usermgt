import {Main, Navbar} from "@cloudogu/ces-theme-tailwind";
import React, {createContext, useContext} from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import {useTranslation} from "react-i18next";
import {createBrowserRouter, Navigate, Outlet, RouterProvider, useLocation} from "react-router-dom";
import usermgtIcon from "./assets/usermgt_icon_detailed.svg";
import {t} from "./helpers/i18nHelpers";
import {useCasUser} from "./hooks/useCasUser";
import Account from "./pages/Account";
import {EditGroup} from "./pages/EditGroup";
import EditUser from "./pages/EditUser";
import ErrorPage from "./pages/Error";
import Groups from "./pages/Groups";
import {NewGroup} from "./pages/NewGroup";
import NewUser from "./pages/NewUser";
import Summaries from "./pages/Summaries";
import Users from "./pages/Users";
import UsersImport from "./pages/UsersImport";
import UsersImportResult from "./pages/UsersImportResult";
import type {CasUser} from "./services/CasUser";

// import i18n (needs to be bundled)
import "./i18n";
import ProtectedResource from "./components/ProtectedResource";
import TitledResource from "./components/TitledResource";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

type ApplicationContextProps = {
    casUser: CasUser;
}
export const ApplicationContext = createContext<ApplicationContextProps>({
    casUser: {
        principal: "default",
        admin: false
    },
});

const router = createBrowserRouter([
    {
        path: "",
        element: <React.StrictMode><ApplicationContainer children={<Outlet/>}/></React.StrictMode>,
        errorElement: <React.StrictMode><ApplicationContainer children={<ErrorPage/>}/></React.StrictMode>,
        children: [
            {
                path: "",
                element: <Navigate to="/account" replace/>
            },
            {
                path: "account",
                element: <TitledResource pageName={t("pages.account")}>
                    <Account/>
                </TitledResource>
            },
            {
                path: "users",
                element: <ProtectedResource pageName={t("pages.users")}>
                    <Users/>
                </ProtectedResource>
            },
            {
                path: "users/import",
                element: <ProtectedResource pageName={t("pages.usersImport")}>
                    <UsersImport/>
                </ProtectedResource>
            },
            {
                path: "users/import/:id",
                element: <ProtectedResource pageName={t("pages.usersImport")}>
                    <UsersImportResult/>
                </ProtectedResource>
            },
            {
                path: "summaries",
                element: <ProtectedResource pageName={t("pages.summaries")}>
                    <Summaries/>
                </ProtectedResource>
            },
            {
                path: "users/:username/edit",
                element: <ProtectedResource pageName={t("pages.usersEdit")}>
                    <EditUser/>
                </ProtectedResource>
            },
            {
                path: "users/new",
                element: <ProtectedResource pageName={t("pages.usersNew")}>
                    <NewUser/>
                </ProtectedResource>
            },
            {
                path: "groups",
                element: <ProtectedResource pageName={t("pages.groups")}>
                    <Groups/>
                </ProtectedResource>
            },
            {
                path: "groups/new",
                element: <ProtectedResource pageName={t("pages.groupsNew")}>
                    <NewGroup/>
                </ProtectedResource>
            },
            {
                path: "groups/:groupName/edit",
                element: <ProtectedResource pageName={t("pages.groupsEdit")}>
                    <EditGroup/>
                </ProtectedResource>
            },
        ],
    },
], {basename: contextPath});

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(<RouterProvider router={router}/>);

export type ApplicationContainerProps = {
    children: React.JSX.Element;
}

function ApplicationContainer({children}: ApplicationContainerProps) {
    const {user: casUser} = useCasUser();
    return <ApplicationContext.Provider value={{casUser: casUser}}>
        <Nav/>
        <Main>
            {children}
        </Main>
    </ApplicationContext.Provider>;
}

function Nav() {
    const location = useLocation();
    const {t} = useTranslation();
    const {casUser} = useContext(ApplicationContext);
    return (
        <>
            <Navbar currentPath={location?.pathname ?? ""}>
                <Navbar.LeftAlignedList>
                    <Navbar.HomeLink homeUri={"/account"}>
                        <div className={"flex items-center mr-2"}>
                            <div className={"rounded-full p-2 bg-nav-primary"}>
                                <img src={usermgtIcon} className={"w-6 h-6"} alt={t("navbar.logoAltText") as string}/>
                            </div>
                        </div>
                        <Navbar.HomeLink.LinkText>{"User Management"}</Navbar.HomeLink.LinkText>
                    </Navbar.HomeLink>
                    {casUser.admin ?
                        <>
                            <Navbar.ListItem path={"/users"}>
                                <Navbar.ListItem.Icon type={"users"} className={"md:hidden"}/>
                                {t("pages.users")}
                            </Navbar.ListItem>
                            <Navbar.ListItem path={"/groups"}>
                                <Navbar.ListItem.Icon type={"groups"} className={"md:hidden"}/>
                                {t("pages.groups")}
                            </Navbar.ListItem>
                            <Navbar.ListItem path={"/users/import"}>
                                <Navbar.ListItem.Icon type={"users"} className={"md:hidden"}/>
                                {t("pages.usersImport")}
                            </Navbar.ListItem>
                            <Navbar.ListItem path={"/summaries"}>
                                <Navbar.ListItem.Icon type={"users"} className={"md:hidden"}/>
                                {t("pages.summaries")}
                            </Navbar.ListItem>
                        </> :
                        <></>
                    }
                </Navbar.LeftAlignedList>
                <Navbar.RightAlignedList>
                    <Navbar.UserLink loggedInUser={{name: casUser.principal, accountUri: "/account"}}/>
                    <Navbar.LogoutLink logoutUri={"/usermgt/api/logout"}>{t("navbar.logout")}</Navbar.LogoutLink>
                </Navbar.RightAlignedList>
            </Navbar>
        </>
    );
}
