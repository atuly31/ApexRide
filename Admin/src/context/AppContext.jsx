// // src/context/AppContext.jsx

// import React, { createContext, useContext, useState, useEffect } from 'react';
// import PropTypes from 'prop-types';

// const AppContext = createContext(undefined);

// export const useApp = () => {
//   const context = useContext(AppContext);
//   if (!context) {
//     throw new Error("useApp must be used within AppProvider");
//   }
//   return context;
// };

// export const AppProvider = ({ children }) => {
//   const [currentUser, setCurrentUser] = useState(null);
//   const [drivers, setDrivers] = useState([]);
//   const [rides, setRides] = useState([]);
//   const [payments, setPayments] = useState([]);
//   const [isLoading, setIsLoading] = useState(false);
//   const [error, setError] = useState(null);

//   // Persist user session in localStorage
//   useEffect(() => {
//     const storedUser = localStorage.getItem('currentUser');
//     if (storedUser) {
//       setCurrentUser(JSON.parse(storedUser));
//     }
//   }, []);

//   useEffect(() => {
//     if (currentUser) {
//       localStorage.setItem('currentUser', JSON.stringify(currentUser));
//     }
//   }, [currentUser]);

//   return (
//     <AppContext.Provider value={{
//       currentUser,
//       setCurrentUser,
//       drivers,
//       setDrivers,
//       rides,
//       setRides,
//       payments,
//       setPayments,
//       isLoading,
//       setIsLoading,
//       error,
//       setError,
//     }}>
//       {children}
//     </AppContext.Provider>
//   );
// };

// AppProvider.propTypes = {
//   children: PropTypes.node.isRequired,
// };










// // // src/context/AppContext.jsx

// // import React, { createContext, useContext, useState, useEffect } from 'react';
// // import PropTypes from 'prop-types';
// // import axios from 'axios'; // Import Axios for making HTTP requests

// // // --- Configuration ---
// // // Define your Spring Boot backend base URL
// // const API_BASE_URL = 'http://localhost:8080/api'; // <--- IMPORTANT: Change this to your actual backend URL

// // // --- JSDoc Type Definitions (for documentation/autocompletion) ---
// // /**
// //  * @typedef {object} User
// //  * @property {string} id
// //  * @property {string} username
// //  * @property {string} firstName
// //  * @property {string} lastName
// //  * @property {string} email
// //  * @property {string} phone
// //  * @property {'user' | 'driver'} userType
// //  */

// // /**
// //  * @typedef {User & {
// //  * licenseNumber: string;
// //  * vehicleType: string;
// //  * vehicleModel: string;
// //  * vehiclePlate: string;
// //  * rating: number;
// //  * totalRides: number;
// //  * isAvailable: boolean;
// //  * }} Driver
// //  */

// // /**
// //  * @typedef {object} Ride
// //  * @property {string} id
// //  * @property {string} userId
// //  * @property {string} [driverId]
// //  * @property {string} pickup
// //  * @property {string} destination
// //  * @property {number} distance
// //  * @property {number} estimatedFare
// //  * @property {number} [actualFare]
// //  * @property {'pending' | 'accepted' | 'in-progress' | 'completed' | 'cancelled'} status
// //  * @property {string} requestTime // Changed from Date to string for API consistency
// //  * @property {string} [acceptedTime]
// //  * @property {string} [completedTime]
// //  * @property {number} [rating]
// //  * @property {string} [review]
// //  */

// // /**
// //  * @typedef {object} Payment
// //  * @property {string} id
// //  * @property {string} rideId
// //  * @property {number} amount
// //  * @property {'card' | 'cash' | 'wallet'} method
// //  * @property {'pending' | 'completed' | 'failed'} status
// //  * @property {string} timestamp // Changed from Date to string for API consistency
// //  */

// // /**
// //  * @typedef {object} AppContextType
// //  * @property {Driver[]} drivers
// //  * @property {Ride[]} rides
// //  * @property {Payment[]} payments
// //  * @property {boolean} isLoading // Added loading state
// //  * @property {string | null} error // Added error state
// //  * @property {() => Promise<void>} fetchDrivers
// //  * @property {(driverData: Omit<Driver, 'id' | 'rating' | 'totalRides' | 'isAvailable' | 'userType'>) => Promise<Driver | null>} registerDriver
// //  * @property {(userData: Omit<User, 'id' | 'userType'>) => Promise<User | null>} registerUser
// //  * @property {() => Promise<void>} fetchRides
// //  * @property {(newRideData: Omit<Ride, 'id' | 'requestTime' | 'status'>) => Promise<Ride | null>} addRide
// //  * @property {(rideId: string, updates: Partial<Ride>) => Promise<Ride | null>} updateRide
// //  * @property {(paymentData: Omit<Payment, 'id' | 'timestamp'>) => Promise<Payment | null>} addPayment
// //  */


// // const AppContext = createContext(undefined);

// // export const useApp = () => {
// //   const context = useContext(AppContext);
// //   if (!context) {
// //     throw new Error("useApp must be used within AppProvider");
// //   }
// //   return context;
// // };

// // export const AppProvider = ({ children }) => {
// //   const [currentUser, setCurrentUser] = useState(null);
// //   const [drivers, setDrivers] = useState([]);
// //   const [rides, setRides] = useState([]);
// //   const [payments, setPayments] = useState([]);
// //   const [isLoading, setIsLoading] = useState(false);
// //   const [error, setError] = useState(null);

// //   const api = axios.create({
// //     baseURL: API_BASE_URL,
// //     headers: {
// //       'Content-Type': 'application/json',
// //       // Authorization header is NOT set here. It should be handled by AuthContext or an Axios Interceptor.
// //     },
// //   });

// //   // Persist user session
// //   useEffect(() => {
// //     const storedUser = localStorage.getItem('currentUser');
// //     if (storedUser) {
// //       setCurrentUser(JSON.parse(storedUser));
// //     }
// //   }, []);

// //   useEffect(() => {
// //     if (currentUser) {
// //       localStorage.setItem('currentUser', JSON.stringify(currentUser));
// //     }
// //   }, [currentUser]);

// //   const fetchDrivers = async () => {
// //     setIsLoading(true);
// //     setError(null);
// //     try {
// //       const response = await api.get('/drivers'); // Your Spring Boot endpoint for drivers
// //       setDrivers(response.data); // Assuming backend returns array of drivers
// //     } catch (err) {
// //       console.error('Failed to fetch drivers:', err);
// //       setError(err.response?.data?.message || 'Failed to fetch drivers');
// //       return null; // Return null on error
// //     } finally {
// //       setIsLoading(false);
// //     }
// //   };

// //   const fetchRides = async (userId, userType) => { // userId and userType might be needed to fetch specific rides
// //     setIsLoading(true);
// //     setError(null);
// //     try {
// //       // You'll need to pass `userId` and `userType` from AuthContext in your components
// //       // when calling this function, as AppContext no longer knows who the user is.
// //       const endpoint = userType === 'driver' ? `/drivers/${userId}/rides` : `/users/${userId}/rides`;
// //       const response = await api.get(endpoint); // Your Spring Boot endpoint for rides
// //       setRides(response.data);
// //     } catch (err) {
// //       console.error('Failed to fetch rides:', err);
// //       setError(err.response?.data?.message || 'Failed to fetch rides');
// //       return null; // Return null on error
// //     } finally {
// //       setIsLoading(false);
// //     }
// //   };

// //   const registerUser = async (userData) => {
// //     setIsLoading(true);
// //     setError(null);
// //     try {
// //       const response = await api.post('/auth/register/user', userData); // Your Spring Boot register user endpoint
// //       const newUser = response.data; // Assuming backend returns the newly created user object
// //       return newUser;
// //     } catch (err) {
// //       console.error("User registration failed:", err);
// //       setError(err.response?.data?.message || "User registration failed.");
// //       return null;
// //     } finally {
// //       setIsLoading(false);
// //     }
// //   };

// //   const registerDriver = async (driverData) => {
// //     setIsLoading(true);
// //     setError(null);
// //     try {
// //       const response = await api.post('/auth/register/driver', driverData); // Your Spring Boot register driver endpoint
// //       const newDriver = response.data; // Assuming backend returns the newly created driver object
// //       setDrivers(prev => [...prev, newDriver]); // Update local state after successful backend creation
// //       return newDriver;
// //     } catch (err) {
// //       console.error("Driver registration failed:", err);
// //       setError(err.response?.data?.message || "Driver registration failed.");
// //       return null;
// //     } finally {
// //       setIsLoading(false);
// //     }
// //   };

// //   const addRide = async (newRideData, userId) => { // userId might be needed for the backend
// //     setIsLoading(true);
// //     setError(null);
// //     try {
// //       const response = await api.post('/rides', {
// //           ...newRideData,
// //           userId: userId // Ensure userId is passed if needed by backend
// //       }); // Your Spring Boot endpoint for adding rides
// //       const createdRide = response.data;
// //       setRides(prev => [...prev, createdRide]); // Update local state
// //       return createdRide;
// //     } catch (err) {
// //       console.error("Failed to add ride:", err);
// //       setError(err.response?.data?.message || "Failed to add ride.");
// //       return null;
// //     } finally {
// //       setIsLoading(false);
// //     }
// //   };

// //   const updateRide = async (rideId, updates) => {
// //     setIsLoading(true);
// //     setError(null);
// //     try {
// //       const response = await api.put(`/rides/${rideId}`, updates);
// //       const updatedRide = response.data;
// //       setRides(prev => prev.map(ride =>
// //         ride.id === rideId ? updatedRide : ride // Replace with the updated object from backend
// //       ));
// //       return updatedRide;
// //     } catch (err) {
// //       console.error(`Failed to update ride ${rideId}:`, err);
// //       setError(err.response?.data?.message || "Failed to update ride.");
// //       return null;
// //     } finally {
// //       setIsLoading(false);
// //     }
// //   };

// //   const addPayment = async (paymentData) => {
// //     setIsLoading(true);
// //     setError(null);
// //     try {
// //       const response = await api.post('/payments', paymentData); // Your Spring Boot endpoint
// //       const newPayment = response.data;
// //       setPayments((prev) => [...prev, newPayment]);
// //       return newPayment;
// //     } catch (err) {
// //       console.error("Failed to add payment:", err);
// //       setError(err.response?.data?.message || "Failed to add payment.");
// //       return null;
// //     } finally {
// //       setIsLoading(false);
// //     }
// //   };

// //   return (
// //     <AppContext.Provider value={{
// //       drivers,
// //       rides,
// //       payments,
// //       isLoading,
// //       error,
// //       fetchDrivers,
// //       fetchRides,
// //       registerUser,
// //       registerDriver,
// //       addRide,
// //       updateRide,
// //       addPayment,
// //     }}>
// //       {children}
// //     </AppContext.Provider>
// //   );
// // };

// // AppProvider.propTypes = {
// //   children: PropTypes.node.isRequired,
// // };
