import { useContext, createContext, useEffect, useState } from "react";
import axios from "axios";

const AuthContext = createContext();

const AuthProvider = ({ children }) => {
    const [token, setTokenInternal] = useState(localStorage.getItem("token"));
    const [user, setUserInternal] = useState(
        JSON.parse(localStorage.getItem("user"))
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

    const updateUserState = (newUser) => {
        if (newUser) {
            localStorage.setItem("user", JSON.stringify(newUser));
        } else {
            localStorage.removeItem("user");
        }
        setUserInternal(newUser);
    };

    useEffect(() => {
        if (token) {
            axios.defaults.headers.common["Authorization"] = "Bearer " + token;
        } else {
            delete axios.defaults.headers.common["Authorization"];
        }
    }, [token]);

    useEffect(() => {
        const fetchRideStatus = async () => {
            if (!user || !user.userId || !token) {
                setIsBookRideDisabled(false); 
                return;
            }

            try {
                const userId = user.userId;
                const USER_RIDES_API_URL = `http://localhost:8086/api/v1/users/rides/${userId}`;
                const ridesRes = await axios.get(USER_RIDES_API_URL, {
                    headers: {
                        'Authorization': `Bearer ${token}`
                    }
                });

                const rides = ridesRes.data.data;
                let disableButton = false; 

                if (rides.length === 0) {
                    disableButton = false;
                } else {
                    const sortedRides = [...rides].sort((a, b) => new Date(b.startTime) - new Date(a.startTime));
                    const mostRecentRide = sortedRides[0];

                    const hasAnyActiveRideCurrently = rides.some(ride =>
                        ride.status === "SEARCHING_DRIVER" ||
                        ride.status === "DRIVER_ASSIGNED" ||
                        ride.status === "RIDE_STARTED"
                    );


                    if (hasAnyActiveRideCurrently) {
                        disableButton = true; // Actively on a ride
                    } else if (mostRecentRide.status !== "COMPLETED") {
                        disableButton = true; // Most recent ride not completed
                    } else {
                        disableButton = false; // Most recent completed, no active rides
                    }
                }
                setIsBookRideDisabled(disableButton);
            } catch (error) {
                console.error("Error fetching ride status in AuthContext:", error);
                if (axios.isAxiosError(error) && error.response && error.response.status === 404) {
                    setIsBookRideDisabled(false); // 404 means no rides, so enable
                } else {
                    setIsBookRideDisabled(true); // Other errors, safer to restrict booking
                }
            }
        };

        fetchRideStatus();
        const pollInterval = setInterval(fetchRideStatus, 15000); // Poll every 15 seconds

        return () => clearInterval(pollInterval);
    }, [user, token]);

    const login = (jwtToken, userData) => {
        updateTokenState(jwtToken);
        updateUserState(userData);
    };

   

    const logout = () => {
        updateTokenState(null);
        updateUserState(null);
        setIsBookRideDisabled(false);
    };
    console.log(isBookRideDisabled)
 
    return (
        <AuthContext.Provider value={{ token, user, login, logout, isBookRideDisabled }}>
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    return useContext(AuthContext);
};

export default AuthProvider;