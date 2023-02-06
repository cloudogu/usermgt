import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import {BrowserRouter as Router, Route, Routes, useLocation} from "react-router-dom";
import Account from "./pages/account/Account";
import Users from "./pages/Users";
import Groups from "./pages/Groups";
import {useUser} from "./hooks/useUser";
import type {Site} from "@cloudogu/ces-theme-tailwind";
import {Main, Navbar} from "@cloudogu/ces-theme-tailwind";
import i18n from 'i18next';

// import i18n (needs to be bundled)
import './i18n';

const contextPath = process.env.PUBLIC_URL || "/usermgt";

const availableSites: Site[] = [
  {name: i18n.t("pages.users"), path: "/users", icon: "users"},
  {name: i18n.t("pages.groups"), path: "/groups", icon: "groups"},
];

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <Router basename={contextPath}>
      <Nav/>
      <Main>
        <Routes>
          <Route
            path="/"

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

  return (
    <Navbar sites={availableSites}
            currentPath={location?.pathname}
            toolName={"User Management"}
            loggedInUser={{name: user.principal, accountUri: '/account'}}
            logoutUri={`/api/logout`}
            homeUri={`/account`}
            contextPath={contextPath}
    />
  )
}
