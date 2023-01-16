import './App.css'
import {Navbar} from "./components/Navbar";
import {Route, Routes} from "react-router-dom";
import {QueryClient, QueryClientProvider} from "react-query";

const availableSites: { name: string, path: string }[] = [
    {name: "Users", path: "users"},
    {name: "Groups", path: "groups"},
];

const queryClient = new QueryClient();

function App() {
    return (
        <div>
            <QueryClientProvider client={queryClient}>
            <Navbar sites={availableSites}></Navbar>
            <main className={"container"}>
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
