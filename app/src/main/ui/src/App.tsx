import './App.css'
import Navbar from "@cloudogu/ces-theme-react/src/components/Navbar";
import {Route, Routes, useLocation} from "react-router-dom";
import {QueryClient, QueryClientProvider} from "react-query";

const availableSites: { name: string, path: string }[] = [
  {name: "Users", path: "/users"},
  {name: "Groups", path: "/groups"},
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
