// services/authService.js
import axios from "axios";

export const loginUser = async (credentials) => {
  const API_URL = "http://localhost:8086/auth/login";
  try {
    const response = await axios.post(API_URL, credentials);
    console.log(response.data);
    return response.data;
  } catch (error) {
    throw error.response?.data || { message: "Login failed" };
  }
};

export const handleProfileUpdate = async (userId, token, updatedUserData) => {
  const API_URL = `http://localhost:8086/api/v1/users/updateProfile/${userId}`;
  console.log("Inside HandleProfile");
  try {
    const response = await axios.put(API_URL, updatedUserData, {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    console.log(response);
    return response.data; // Return data for success feedback in component
  } catch (error) {
    console.error("Error updating profile:", error);
    throw error.response?.data || { message: "Profile update failed" }; // Re-throw for handling in component
  }
};

export const findUserRideLatestRide = async (userId,token) => {
  const API_GATEWAY_URL = "http://localhost:8086/api/v1/rides/latest-assigned-by-user/"; 
  try {
    const USER_RIDES_API_URL = `${API_GATEWAY_URL}${userId}`;
    const ridesRes = await axios.get(USER_RIDES_API_URL, {
      headers: {
        'Authorization': `Bearer ${token}` 
      }
    });
    console.log(ridesRes)

    return ridesRes.data;
  } catch (error) {
  
    console.error("Error in findUserRideDetails:", error);
  
    throw error;
  }
}

export const submitRating = async (rideId, rating, token) => {
  const  API_GATEWAY_URL  = "http://localhost:8086";
 
  const RATING_API_URL = `${API_GATEWAY_URL}/api/v1/rides/rating/${rideId}/${rating}`;

  try {
    const response = await axios.put(RATING_API_URL, {}, { // Empty object for data, then config
      headers: {
        'Authorization': `Bearer ${token}` // Include the authorization token in the headers
      }
    });

    console.log("Rating submission successful:", response.data);
    return response.data;

  } catch (error) {
    console.error("Error in submitRating:", error);
    if (axios.isAxiosError(error)) {
     
      if (error.response) {
        console.error("Response data:", error.response.data);
        console.error("Response status:", error.response.status);
        console.error("Response headers:", error.response.headers);
        throw new Error(`Rating submission failed: ${error.response.status} - ${error.response.data?.message || error.message}`);
      } else if (error.request) {
        console.error("No response received:", error.request);
        throw new Error("Network Error: No response from server when submitting rating.");
      } else {
        console.error("Error setting up request:", error.message);
        throw new Error(`Request Error: ${error.message}`);
      }
    } else {
      throw new Error(`An unexpected error occurred: ${error.message}`);
    }
  }

}



