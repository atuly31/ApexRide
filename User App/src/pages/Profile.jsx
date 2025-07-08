import React, { useState, useEffect } from "react";
import { User, Mail, Phone, Edit3, Save, X } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Form,
  ListGroup,
  Alert,
} from "react-bootstrap";
import axios from "axios";
import { handleProfileUpdate } from "../service/authService";
import { useNavigate } from "react-router-dom"; 

const Profile = () => {
  const { token, user, updateUser,logout } = useAuth(); 
  const nav = useNavigate();

  const [currentUser, setCurrentUser] = useState(null);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    userName: "", 
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
  });
  const [loadingProfile, setLoadingProfile] = useState(true);
  const [profileError, setProfileError] = useState(null);
  const [updateLoading, setUpdateLoading] = useState(false);
  const [updateError, setUpdateError] = useState(null);
  const [updateSuccess, setUpdateSuccess] = useState(false);

  useEffect(() => {
    const fetchUserProfile = async () => {
      setLoadingProfile(true);
      setProfileError(null);

      if (!user || !user.userId || !token) {
        setLoadingProfile(false);
        setProfileError("Authentication details missing. Please log in.");
        console.error("Authentication details (user or token) are missing for profile fetch.");
        return;
      }

      const userId = user.userId;
      const USER_PROFILE_API_URL = `http://localhost:8086/api/v1/users/${userId}`;
      console.log("Fetching profile for userId:", userId);
      console.log("Current Token:", token); 

      try {
        const response = await axios.get(USER_PROFILE_API_URL, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        console.log("User Profile API Response:", response.data);
        const userProfileResponse = response.data.data;

        setCurrentUser(userProfileResponse);
        setFormData({
          userName: userProfileResponse.userName || "", 
          firstName: userProfileResponse.firstName || "",
          lastName: userProfileResponse.lastName || "",
          email: userProfileResponse.email || "",
          phoneNumber: userProfileResponse.phoneNumber || "",
        });

      } catch (error) {
        console.error("Error fetching user profile:", error);
        setProfileError(`Failed to load profile: ${error.response?.data?.message || error.message}`);
      } finally {
        setLoadingProfile(false);
      }
    };

    fetchUserProfile();
  }, [token, user]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSave = async () => {
    setUpdateLoading(true);
    setUpdateError(null);
    setUpdateSuccess(false);

    try {
      const response = await handleProfileUpdate(user.userId, token, formData);
      setCurrentUser((prevUser) => ({
        ...prevUser,
        ...formData,
      }));

      
      if (updateUser) {
          updateUser(formData); 
      }

      setIsEditing(false); 
      setUpdateSuccess(true); 
      console.log("Profile updated successfully!", response);

      setTimeout(() => {
        logout();
        nav("/login"); 
      }, 2500); 

    } catch (error) {
      console.error("Error saving profile:", error);
      setUpdateError(error.message || "Failed to update profile.");
    } finally {
      setUpdateLoading(false);
    }
  };

  const handleCancel = () => {
    setFormData({
      userName: currentUser?.userName || "", 
      firstName: currentUser?.firstName || "",
      lastName: currentUser?.lastName || "",
      email: currentUser?.email || "",
      phoneNumber: currentUser?.phoneNumber || "",
    });
    setIsEditing(false);
    setUpdateError(null); 
    setUpdateSuccess(false); 
  };

  if (loadingProfile) {
    return <Container className="my-4 py-4" style={{ maxWidth: "960px" }}>Loading profile...</Container>;
  }

  if (profileError) {
    return (
      <Container className="my-4 py-4" style={{ maxWidth: "960px" }}>
        <Alert variant="danger">{profileError}</Alert>
      </Container>
    );
  }

  return (
    <Container className="my-4 py-4" style={{ maxWidth: "960px" }}>
      <Row className="mb-4 d-flex justify-content-between align-items-center">
        <Col>
          <h1 className="fs-2 fw-bold text-dark">Profile</h1>
        </Col>
        <Col xs="auto">
          {!isEditing ? (
            <Button
              onClick={() => setIsEditing(true)}
              variant="primary"
              className="d-inline-flex align-items-center px-3 py-2 fw-medium"
            >
              <Edit3 size={16} className="me-2" />
              <span>Edit Profile</span>
            </Button>
          ) : (
            <div className="d-flex gap-2">
              <Button
                onClick={handleSave}
                variant="success"
                className="d-inline-flex align-items-center px-3 py-2 fw-medium"
                disabled={updateLoading}
              >
                {updateLoading ? (
                  <>
                    <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    <span>Saving...</span>
                  </>
                ) : (
                  <>
                    <Save size={16} className="me-2" />
                    <span>Save</span>
                  </>
                )}
              </Button>
              <Button
                onClick={handleCancel}
                variant="secondary"
                className="d-inline-flex align-items-center px-3 py-2 fw-medium"
                disabled={updateLoading}
              >
                <X size={16} className="me-2" />
                <span>Cancel</span>
              </Button>
            </div>
          )}
        </Col>
      </Row>

      {/* Display update messages conditionally */}
      {updateError && <Alert variant="danger">{updateError}</Alert>}
      {updateSuccess && <Alert variant="success">Profile updated successfully!</Alert>}

      <Card className="shadow-sm rounded-3 p-4 mb-4">
        <Card.Header className="bg-white border-0 px-0 pt-0 pb-3">
          <h2 className="fs-5 fw-semibold text-dark mb-0">
            Personal Information
          </h2>
        </Card.Header>

        <Row className="g-4">
          <Col md={6}>
            <Form.Group className="mb-0">
              <Form.Label className="small fw-medium text-muted mb-1">
                Username
              </Form.Label>
              {isEditing ? (
                <Form.Control
                  type="text"
                  name="userName" 
                  value={formData.userName}
                  onChange={handleChange}
                />
              ) : (
                <div className="d-flex align-items-center bg-light p-3 rounded">
                  <User size={20} className="text-muted me-3" />
                  <span>{currentUser?.userName || "N/A"}</span> 
                </div>
              )}
            </Form.Group>
          </Col>

          <Col md={6}>
            <Form.Group className="mb-0">
              <Form.Label className="small fw-medium text-muted mb-1">
                First Name
              </Form.Label>
              {isEditing ? (
                <Form.Control
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                />
              ) : (
                <div className="d-flex align-items-center bg-light p-3 rounded">
                  <User size={20} className="text-muted me-3" />
                  <span>{currentUser?.firstName || "N/A"}</span>
                </div>
              )}
            </Form.Group>
          </Col>

          <Col md={6}>
            <Form.Group className="mb-0">
              <Form.Label className="small fw-medium text-muted mb-1">
                Last Name
              </Form.Label>
              {isEditing ? (
                <Form.Control
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                />
              ) : (
                <div className="d-flex align-items-center bg-light p-3 rounded">
                  <User size={20} className="text-muted me-3" />
                  <span>{currentUser?.lastName || "N/A"}</span>
                </div>
              )}
            </Form.Group>
          </Col>

          <Col md={6}>
            <Form.Group className="mb-0">
              <Form.Label className="small fw-medium text-muted mb-1">
                Email Address
              </Form.Label>
              {isEditing ? (
                <Form.Control
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                />
              ) : (
                <div className="d-flex align-items-center bg-light p-3 rounded">
                  <Mail size={20} className="text-muted me-3" />
                  <span>{currentUser?.email || "N/A"}</span>
                </div>
              )}
            </Form.Group>
          </Col>

          <Col md={6}>
            <Form.Group className="mb-0">
              <Form.Label className="small fw-medium text-muted mb-1">
                Phone Number
              </Form.Label>
              {isEditing ? (
                <Form.Control
                  type="tel"
                  name="phoneNumber"
                  value={formData.phoneNumber}
                  onChange={handleChange}
                />
              ) : (
                <div className="d-flex align-items-center bg-light p-3 rounded">
                  <Phone size={20} className="text-muted me-3" />
                  <span>{currentUser?.phoneNumber || "N/A"}</span>
                </div>
              )}
            </Form.Group>
          </Col>
        </Row>
      </Card>

     
    </Container>
  );
};

export default Profile;