import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Layout from "./components/Layout.jsx";
import Home from "./pages/Home.jsx";
import Login from "./pages/Login.jsx";
import DriverRegister from "./pages/DriverRegister.jsx";
import DriverDashboard from "./pages/DriverDashboard.jsx";
import DriverProfile from "./pages/DriverProfile.jsx";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/driver-register" element={<DriverRegister />} />

    
        <Route
          path="/driver-dashboard"
          element={
            <Layout>
              <DriverDashboard />
            </Layout>
          }
        />

        <Route
          path="/driver-profile"
          element={
            <Layout>
              <DriverProfile/>
            </Layout>
          }
        />
  
      </Routes>
    </Router>
  );
}

export default App;