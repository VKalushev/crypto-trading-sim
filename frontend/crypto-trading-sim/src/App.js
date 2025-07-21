import React from "react";
import { Routes, Route, Navigate, useLocation } from "react-router-dom";
import LoginPage from "./pages/Login";
import RegisterPage from "./pages/Register";
import HomePage from "./pages/HomePage";
import Layout from "./components/Layout";

const isAuthenticated = () => !!localStorage.getItem("token");

function App() {
  const location = useLocation();

  return (
    <Routes>
      <Route
        path="/login"
        element={
          isAuthenticated() ? (
            <Navigate to="/" replace />
          ) : (
            <LoginPage />
          )
        }
      />
      <Route
        path="/register"
        element={
          isAuthenticated() ? (
            <Navigate to="/" replace />
          ) : (
            <RegisterPage />
          )
        }
      />
      <Route
        path="/"
        element={
          isAuthenticated() ? (
            <Layout>
              <HomePage />
            </Layout>
          ) : (
            <Navigate to="/login" replace state={{ from: location }} />
          )
        }
      />
    </Routes>
  );
}

export default App;
