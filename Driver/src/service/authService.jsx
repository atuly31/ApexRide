import axios from "axios";

export const loginDriver = async (credentials) => {
  const API_URL = "http://localhost:8086/auth/login";
  try {
    const response = await axios.post(API_URL, credentials);
    console.log(response.data);
    return response.data;
  } catch (error) {
    throw error.response?.data || { message: "Login failed" };
  }
};

export const acceptRide = async (driverID, token) => {
    const API_URL = `http://localhost:8086/api/v1/drivers/start-ride/${driverID}`;
    try {
        const response = await axios.patch(
            API_URL,
            {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json' 
                }
            }
        );
        console.log("Ride acceptance response:", response.data);
        return response.data;

    } catch (error) {
       
        console.error("Error accepting ride:", error.response?.data || error.message);
        throw error.response?.data || { message: "Failed to accept ride." };
    }
};

export const completeRide = async (rideId, driverId, token) => {
  
    const API_URL = `http://localhost:8086/api/v1/drivers/complete-ride/${driverId}/${rideId}`;

    try {
       
        const response = await axios.put(
            API_URL,
            {
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                }
            }
        );
        console.log("Ride completion response:", response.data);
        return response.data;
    } catch (error) {
        console.error("Error completing ride:", error.response?.data || error.message);
        throw error.response?.data || { message: "Failed to complete ride." };
    }
};
