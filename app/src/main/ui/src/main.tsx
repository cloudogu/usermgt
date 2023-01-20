import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App'
import './index.css'
import {BrowserRouter as Router, Route, Routes} from "react-router-dom";

const contextPath = process.env.PUBLIC_URL || "/usermgt";

ReactDOM.createRoot(document.getElementById('root') as HTMLElement).render(
  <React.StrictMode>
    <Router basename={contextPath}>
      <Routes>
        <Route path="/logging" element={<div/>}/>
        <Route index path="/" element={<div/>}/>
        <Route path="/account" element={<div/>}/>
        <Route path="/users" element={<div/>}/>
        <Route path="/groups" element={<div/>}/>
      </Routes>
      <App/>
    </Router>
  </React.StrictMode>,
)
