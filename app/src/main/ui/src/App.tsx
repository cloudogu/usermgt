import './App.css'
import {Route, Routes, useLocation} from "react-router-dom";
import {QueryClient, QueryClientProvider} from "react-query";
import Navbar, {Site} from "@cloudogu/ces-theme-tailwind/src/components/Navbar";

const availableSites: Site[] = [
  {name: "Users", path: "/users", icon: "users"},
  {name: "Groups", path: "/groups", icon: "groups"},
];

const queryClient = new QueryClient();

function App() {
  const location = useLocation();
  console.log(location?.pathname);

  return (
    <div>
      <QueryClientProvider client={queryClient}>
        <Navbar sites={availableSites}
                currentPath={location?.pathname}
                toolName={"User Management"}
                loggedInUser={{name: "testadmionuser"}}
                logoutUri={"asdf"}/>
        <main className={"container text-base"}>
          <Routes>
            <Route path="/logging" element={<div/>}/>
            <Route index path="/" element={<div/>}/>
            <Route path="/account" element={<div/>}/>
            <Route path="/users" element={<div/>}/>
            <Route path="/groups" element={<div/>}/>
          </Routes>
        </main>
      </QueryClientProvider>
    </div>
  )
}

export default App
