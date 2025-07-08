import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Layout from "./components/Layout.jsx";

import Home from "./pages/Home.jsx";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import Dashboard from "./pages/Dashboard.jsx";
import BookRide from "./pages/BookRide.jsx";
import Payment from "./pages/Payment.jsx";
import Profile from "./pages/Profile.jsx";
import PaymentSuccess from "./pages/PaymentSuccess.jsx";
import RatingPage from "./pages/Rating.jsx";
function App() {
  return (
    <Router>
      <Routes>
        {/* Public routes */}
        <Route path="/" element={<Home />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Routes with layout */}
        <Route
          path="/dashboard"
          element={
            <Layout>
              <Dashboard />
            </Layout>
          }
        />
        <Route
          path="/book-ride"
          element={
            <Layout>
              <BookRide />
            </Layout>
          }
        />
        <Route
          path="/payment"
          element={
            <Layout>
              <Payment />
            </Layout>
          }
        />
        <Route path="/rating"
        element={<Layout>
          <RatingPage/>
        </Layout>
         
        }
        />
         <Route
          path="/payment"
          element={
            <Layout>
              <Payment />
            </Layout>
          }
        />
        <Route
          path="/profile"
          element={
            <Layout>
              <Profile />
            </Layout>
          }
        />
         <Route path="/payment-success" element={<PaymentSuccess />} />
      </Routes>
    </Router>
  );
}

export default App;
