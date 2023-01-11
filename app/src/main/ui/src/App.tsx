import './App.css'
import {Navbar} from "./components/Navbar";
import {Route, Routes} from "react-router-dom";

const availableSites: { name: string, path: string }[] = [
    {name: "Account", path: "account"},
    {name: "Users", path: "users"},
    {name: "Groups", path: "groups"},
];

function App() {
    return (
        <div>
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
        </div>
    )
}

export default App
