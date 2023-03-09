import {Main, Navbar} from "@cloudogu/ces-theme-tailwind";
import React, {createContext, useContext} from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import {useTranslation} from "react-i18next";
import {Outlet, useLocation, createBrowserRouter, RouterProvider, Navigate} from "react-router-dom";
import usermgtIcon from "./assets/usermgt_icon_detailed.svg";
import {t} from "./helpers/i18nHelpers";
import {useCasUser} from "./hooks/useCasUser";
import Account from "./pages/account/Account";
import ErrorPage from "./pages/error/Error";
import {EditGroup} from "./pages/groups/EditGroup";
import Groups from "./pages/groups/Groups";
import {NewGroup} from "./pages/groups/NewGroup";
import Users from "./pages/users/Users";
import type {CasUser} from "./services/CasUser";

// import i18n (needs to be bundled)
import "./i18n";
import EditUser from "./pages/users/EditUser";

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
        element: <MainApplication />,
        errorElement: <ErrorPage />,
        children: [
            {
                path: "",
                element: <Navigate to="/account" replace />,
            },
            {
                path: "account",
                element: <Account title={t("pages.account") + " | User Management"}/>,
            },
            {
                path: "users",
                element: <Users title={t("pages.users") + " | User Management"}/>,
            },
            {
                path: "groups",
                element: <Groups title={t("pages.groups") + " | User Management"}/>,
            },
            {
                path: "groups/new",
                element: <NewGroup title={t("pages.groupsNew") + " | User Management"}/>
            },
            {
                path: "groups/:groupName/edit",
                element: <EditGroup title={t("pages.groupsEdit") + " | User Management"}/>
            },
            {
                path: "users/:username/edit",
                element: <EditUser title={t("pages.usersEdit") + " | User Management"}/>
            }
        ],
    },
], {basename: contextPath});

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(<React.StrictMode>
    <RouterProvider router={router} />
</React.StrictMode>);

function MainApplication() {
    const {user:casUser} = useCasUser();
    return <ApplicationContext.Provider value={{casUser: casUser}}>
        <Nav/>
        <Main>
            <Outlet />
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
