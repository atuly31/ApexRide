import axios from 'axios';

const BASE_API_URL = "http://localhost:8086/auth";

const registerDriver = async (driverData) => {
    try {
        console.log("Sending user data:", driverData);
        const response = await axios.post(`${BASE_API_URL}/register/driver`, driverData);

        
        if (response.status == 200 || response.status == 201) {
            const backendResponse = response.data; 
            console.log("Full backend response:", backendResponse);

           
            const driverDataFromResponse = backendResponse.data.data;

            return driverDataFromResponse; // Return the full backend response for further handling if needed
        } else {
            console.warn(`Unexpected status code: ${response.status}`);
            throw new Error(`Registration failed with status code ${response.status}`);
        }
    } catch (error) {
        const errorMessage = error.response?.data?.message || error.message || "Unknown error occurred";
        console.error("Registration error:", errorMessage);
        throw new Error(errorMessage);
    }
};

const registerService = {
    registerDriver,
};

export default registerService;
