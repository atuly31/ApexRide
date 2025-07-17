import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Layout from "./components/Layout.jsx";
import Admin from "./pages/Admin.jsx";
import UnapprovedDrivers from "./pages/UnapprovedDrivers.jsx"; // ✅ Import new page

function App() {
  return (
    <Router>
      <Routes>
        <Route
          path="/"
          element={
            <Layout>
              <Admin />
            </Layout>
          }
        />
        <Route
          path="/unapproved-drivers"
          element={
            <Layout>
              <UnapprovedDrivers />
            </Layout>
          }
        />
      </Routes>
    </Router>
  );
}

export default App;
