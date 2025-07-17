import axios from 'axios';

const BASE_API_URL = "http://localhost:8086/auth"; //baseAPI gateway URL

const registerUser = async (userData) => {
    try {
        console.log("Sending user data:", userData);
        const response = await axios.post(`${BASE_API_URL}/register/user`, userData);
        if (response.status == 200 || response.status == 201) {

            const backendResponse = response.data; 
            console.log("Full backend response:", backendResponse);
             const userDataFromResponse = backendResponse.data;
            return backendResponse; 

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
    registerUser,
};

export default registerService;
