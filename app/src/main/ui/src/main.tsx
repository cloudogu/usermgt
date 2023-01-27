import React from 'react'
import ReactDOM from 'react-dom/client'
import './index.css'
import {BrowserRouter as Router, Route, Routes, useLocation} from "react-router-dom";
import Account from "./pages/Account";
import Users from "./pages/Users";
import Groups from "./pages/Groups";
import {useUser} from "./hooks/useUser";
import Navbar, {Site} from "@cloudogu/ces-theme-tailwind/src/components/navbar/Navbar";
import Main from "../ces-theme-tailwind/src/components/Main";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

const availableSites: Site[] = [
  {name: "Users", path: "/users", icon: "users"},
  {name: "Groups", path: "/groups", icon: "groups"},
];

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <Router basename={contextPath}>
      <Nav/>
      <Main>
        <Routes>
          <Route index path="/account" element={<Account/>}/>
          <Route path="/users" element={<Users/>}/>
          <Route path="/groups" element={<Groups/>}/>
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
