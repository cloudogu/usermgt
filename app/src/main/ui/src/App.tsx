import './App.css'
import {useLocation} from "react-router-dom";
import Navbar, {Site} from "@cloudogu/ces-theme-tailwind/src/components/Navbar";
import {useUser} from "./hooks/useUser";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

const availableSites: Site[] = [
  {name: "Users", path: "/users", icon: "users"},
  {name: "Groups", path: "/groups", icon: "groups"},
];

function App() {
  const location = useLocation();
  const user = useUser();

  return (
    <>
      <Navbar sites={availableSites}
              currentPath={location?.pathname}
              toolName={"User Management"}
              loggedInUser={{name: user.principal, accountUri: '/account'}}
              logoutUri={`${contextPath}/api/logout`}
              homeUri={`/account`}
      />
    </>
  )
}

export default App
