// src/context/AuthContext.js - (This file remains mostly the same as the previous response)
import { useContext, createContext, useEffect, useState } from "react";
import axios from "axios";

const AuthContext = createContext();

const AuthProvider = ({ children }) => {
    const [token, setTokenInternal] = useState(localStorage.getItem("token"));
    const [driver, setDriverInternal] = useState(
        JSON.parse(localStorage.getItem("driver"))
    );
    const [isBookRideDisabled, setIsBookRideDisabled] = useState(false); 
    const updateTokenState = (newToken) => {
        if (newToken) {
            localStorage.setItem("token", newToken);
            axios.defaults.headers.common["Authorization"] = "Bearer " + newToken;
        } else {
            localStorage.removeItem("token");
            delete axios.defaults.headers.common["Authorization"];
        }
        setTokenInternal(newToken);
    };

    const updateDriverState = (newDriver) => {
        if (newDriver) {
            localStorage.setItem("driver", JSON.stringify(newDriver));
        } else {
            localStorage.removeItem("driver");
        }
        setDriverInternal(newDriver);
    };

    useEffect(() => {
        if (token) {
            axios.defaults.headers.common["Authorization"] = "Bearer " + token;
        } else {
            delete axios.defaults.headers.common["Authorization"];
        }
    }, [token]);

   

    const login = (jwtToken, driverData) => {
        updateTokenState(jwtToken);
        updateDriverState(driverData);
    };

   

    const logout = () => {
        updateTokenState(null);
        updateDriverState(null);
        setIsBookRideDisabled(false); 
    };
    console.log(isBookRideDisabled)
 
    return (
        <AuthContext.Provider value={{ token, driver, login, logout, isBookRideDisabled }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};

export default AuthProvider;