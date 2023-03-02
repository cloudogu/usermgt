import React, {createContext, useContext} from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import {BrowserRouter as Router, Navigate, Route, Routes, useLocation} from "react-router-dom";
import Account from "./pages/account/Account";
import Users from "./pages/users/Users";
import Groups from "./pages/groups/Groups";
import {useCasUser} from "./hooks/useCasUser";
import {Main, Navbar} from "@cloudogu/ces-theme-tailwind";
import usermgtIcon from './assets/usermgt_icon_detailed.svg';

// import i18n (needs to be bundled)
import './i18n';
import {useTranslation} from "react-i18next";
import {t} from "./helpers/i18nHelpers";
import {CasUser} from "./services/CasUser";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

type ApplicationContextProps = {
    casUser: CasUser;
}
export const ApplicationContext = createContext<ApplicationContextProps>({
    casUser: {
        principal: "default",
        admin: false
    }
});

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(<React.StrictMode>
    <SuperMain/>
</React.StrictMode>)

function SuperMain() {
    const [casUser] = useCasUser();
    return <Router basename={contextPath}>
        <ApplicationContext.Provider value={{casUser: casUser}}>
            <Nav/>
            <Main>
                <Routes>
                    <Route
                        path="/"
                        element={<Navigate to={"/account"}></Navigate>}
                    />
                    <Route index path="/account"
                           element={<Account title={t("pages.account") + " | User Management"}/>}/>
                    <Route path="/users" element={<Users title={t("pages.users") + " | User Management"}/>}/>
                    <Route path="/groups" element={<Groups title={t("pages.groups") + " | User Management"}/>}/>
                </Routes>
            </Main>
        </ApplicationContext.Provider>
    </Router>;
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
                    <Navbar.UserLink loggedInUser={{name: casUser.principal, accountUri: '/account'}}/>
                    <Navbar.LogoutLink logoutUri={`/usermgt/api/logout`}>{t("navbar.logout")}</Navbar.LogoutLink>
                </Navbar.RightAlignedList>
            </Navbar>
        </>
    )
}
