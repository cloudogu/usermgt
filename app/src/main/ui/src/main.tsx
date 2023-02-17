import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import {BrowserRouter as Router, Navigate, Route, Routes, useLocation} from "react-router-dom";
import Account from "./pages/account/Account";
import Users from "./pages/Users";
import Groups from "./pages/Groups";
import {useUser} from "./hooks/useUser";
import {Main, Navbar} from "@cloudogu/ces-theme-tailwind";
import i18n from 'i18next';
import usermgtIcon from './assets/usermgt_icon_detailed.svg';

// import i18n (needs to be bundled)
import './i18n';
import {useTranslation} from "react-i18next";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <Router basename={contextPath}>
      <Nav/>
      <Main>
        <Routes>
          <Route
            path="/"
            element={<Navigate to={"/account"}></Navigate>}
          />
          <Route index path="/account"
                 element={<Account title={i18n.t("pages.account") + " | User Management"}/>}/>
          <Route path="/users" element={<Users title={i18n.t("pages.users") + " | User Management"}/>}/>
          <Route path="/groups" element={<Groups title={i18n.t("pages.groups") + " | User Management"}/>}/>
        </Routes>
      </Main>
    </Router>
  </React.StrictMode>,
)

function Nav() {
  const location = useLocation();
  const user = useUser();
  const {t} = useTranslation();

  return (
    <>
      <Navbar currentPath={location?.pathname}>
        <Navbar.LeftAlignedList>
          <Navbar.HomeLink homeUri={"/account"}>
            <div className={"flex items-center mr-2"}>
              <div className={"rounded-full p-2 bg-nav-primary"}>
                <img src={usermgtIcon} className={"w-6 h-6"} alt={t("navbar.logoAltText") as string}/>
              </div>
            </div>
            <Navbar.HomeLink.LinkText>{"User Management"}</Navbar.HomeLink.LinkText>
          </Navbar.HomeLink>
          <Navbar.ListItem path={"/users"}>
            <Navbar.ListItem.Icon type={"users"} className={"md:hidden"}/>
            {t("pages.users")}
          </Navbar.ListItem>
          <Navbar.ListItem path={"/groups"}>
            <Navbar.ListItem.Icon type={"groups"} className={"md:hidden"}/>
            {t("pages.groups")}
          </Navbar.ListItem>
        </Navbar.LeftAlignedList>
        <Navbar.RightAlignedList>
          <Navbar.LogoutLink logoutUri={`/usermgt/api/logout`}>{t("navbar.logout")}</Navbar.LogoutLink>
          <Navbar.UserLink loggedInUser={{name: user.principal, accountUri: '/account'}}/>
        </Navbar.RightAlignedList>
      </Navbar>
  </>
  )
}
