import React, { useState, useEffect, useCallback } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import { User, Mail, Phone, Car, CheckCircle, XCircle } from "lucide-react"; // Removed Edit3, Save, X icons
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Form,
  Dropdown,
  Spinner,
  Alert,
  Badge,
} from "react-bootstrap";
 
const DriverProfile = () => {
  const { driver, token, logout } = useAuth();
  const navigate = useNavigate();
 
  const [driverData, setDriverData] = useState(null);
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    userName: "",
    phoneNumber: "",
  });
 

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
 
  const getDisplayStatus = useCallback((backendStatus) => {
    switch (backendStatus) {
      case "AVAILABLE":
        return "Available";
      case "ON_RIDE":
        return "On Ride";
      case "OFFLINE":
        return "Offline";
      default:
        return "Unknown";
    }
  }, []);
 
  const fetchDriverProfile = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      if (!driver || !driver.userId) {
        console.error("Driver info not found. Cannot fetch profile.");
        setLoading(false);
        setError("User not logged in or driver ID missing.");
        logout();
        navigate("/login");
        return;
      }
 
      const driverId = driver.userId;
      const API_URL = `http://localhost:8086/api/v1/drivers/${driverId}`;
      console.log("Fetching driver profile from:", API_URL);
 
      const response = await axios.get(API_URL, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
 
      const fetchedData = response.data.data;
      setDriverData(fetchedData);
      setFormData({
        firstName: fetchedData.firstName || "",
        lastName: fetchedData.lastName || "",
        email: fetchedData.email || "",
        userName: fetchedData.userName || "",
        phoneNumber: fetchedData.phoneNumber || "",
      });
      console.log("Driver profile fetched and states updated:", fetchedData);
    } catch (err) {
      console.error("Error fetching driver profile:", err);
      if (axios.isAxiosError(err) && (err.response?.status === 401 || err.response?.status === 403)) {
        alert("Session expired or unauthorized. Please log in again.");
        logout();
        navigate("/login");
      } else {
        setError(`Failed to load profile: ${err.response?.data?.message || err.message}`);
      }
    } finally {
      setLoading(false);
    }
  }, [driver, token, logout, navigate]);
 
  useEffect(() => {
    if (token && driver?.userId) {
      fetchDriverProfile();
    } else {
      setLoading(false);
      setError("Not authenticated. Please log in.");
    }
  }, [token, driver, fetchDriverProfile]);
 
  // Removed handleSave as edit functionality is removed
  // const handleSave = useCallback(async () => {
  //   setLoading(true);
  //   setError(null);
  //   try {
  //     if (!driver || !driver.userId) {
  //       alert("Authentication error. Please log in again.");
  //       logout();
  //       navigate("/login");
  //       return;
  //     }
 
  //     const driverId = driver.userId;
  //     const API_URL = `http://localhost:8086/api/v1/drivers/${driverId}`;
  //     console.log("Saving profile to:", API_URL, "with data:", formData);
 
  //     await axios.patch(API_URL, formData, {
  //       headers: {
  //         Authorization: `Bearer ${token}`,
  //         "Content-Type": "application/json",
  //       },
  //     });
  //     alert("Profile updated successfully!");
  //     setIsEditing(false);
  //     fetchDriverProfile(); // Re-fetch to get the latest data
  //   } catch (err) {
  //     console.error("Error saving driver profile:", err);
  //     setError(`Failed to save profile: ${err.response?.data?.message || err.message}`);
  //     alert(`Failed to save profile: ${err.response?.data?.message || "Unknown error"}`);
  //   } finally {
  //     setLoading(false);
  //   }
  // }, [driver, token, logout, navigate, formData, fetchDriverProfile]);
 
  const handleStatusChange = useCallback(async (selectedStatusKey) => {
    if (!driverData.approved) {
      alert("Cannot change status: Your profile is not yet approved by the admin.");
      return;
    }
 
    setLoading(true);
    setError(null);
    try {
      if (!driver || !driver.userId) {
        alert("Authentication error. Please log in again.");
        logout();
        navigate("/login");
        return;
      }
 
      const driverId = driver.userId;
      let backendStatus;
      if (selectedStatusKey === "Available") {
        backendStatus = "AVAILABLE";
      } else if (selectedStatusKey === "Offline") {
        backendStatus = "OFFLINE";
      } else {
        console.warn("Attempted to set unknown status:", selectedStatusKey);
        setLoading(false);
        return;
      }
 
      const STATUS_API_URL = `http://localhost:8086/api/v1/drivers/status/${driverId}?status=${backendStatus}`;
      console.log("Updating status via:", STATUS_API_URL);
 
      await axios.put(STATUS_API_URL, {}, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      alert(`Status changed to ${getDisplayStatus(backendStatus)}!`);
      fetchDriverProfile();
    } catch (err) {
      console.error("Error updating driver status:", err);
      setError(`Failed to update status: ${err.response?.data?.message || err.message}`);
      alert(`Failed to update status: ${err.response?.data?.message || "Unknown error"}`);
    } finally {
      setLoading(false);
    }
  }, [driver, token, logout, navigate, fetchDriverProfile, getDisplayStatus, driverData]);
 
  // Removed handleCancel as edit functionality is removed
  // const handleCancel = () => {
  //   if (driverData) {
  //     setFormData({
  //       firstName: driverData.firstName || "",
  //       lastName: driverData.lastName || "",
  //       email: driverData.email || "",
  //       userName: driverData.userName || "",
  //       phoneNumber: driverData.phoneNumber || "",
  //     });
  //   }
  //   setIsEditing(false);
  // };
 
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };
 
  if (loading) {
    return (
      <Container className="text-center py-5">
        <Spinner animation="border" role="status">
          <span className="visually-hidden">Loading profile...</span>
        </Spinner>
        <p className="text-muted mt-2">Loading driver profile...</p>
      </Container>
    );
  }
 
  if (error) {
    return (
      <Container className="text-center py-5">
        <Alert variant="danger">
          <Alert.Heading>Error!</Alert.Heading>
          <p>{error}</p>
          <hr />
          <Button onClick={fetchDriverProfile}>Retry Loading Profile</Button>
          <Button variant="secondary" className="ms-2" onClick={() => navigate("/login")}>Go to Login</Button>
        </Alert>
      </Container>
    );
  }
 
  if (!driverData) {
    return (
      <Container className="text-center py-5">
        <Alert variant="info">
          <Alert.Heading>No Profile Found</Alert.Heading>
          <p>Could not retrieve driver profile data. Please ensure you are logged in correctly.</p>
          <hr />
          <Button onClick={() => navigate("/login")}>Go to Login</Button>
        </Alert>
      </Container>
    );
  }
 
  const currentDriverStatus = driverData ? getDisplayStatus(driverData.status) : "Unknown";
  const isStatusActive = driverData?.status === "AVAILABLE" || driverData?.status === "ON_RIDE";
  const isDriverApproved = driverData.approved;
 
  return (
    <Container className="my-4 py-4" style={{ maxWidth: "960px" }}>
      <Row className="mb-4 d-flex justify-content-between align-items-center">
        <Col>
          <h1 className="fs-2 fw-bold text-dark">Driver Profile</h1>
        </Col>
        <Col xs="auto">
          {/* Removed conditional rendering for Edit/Save/Cancel buttons */}
          {/* {!isEditing ? (
            <Button
              onClick={() => setIsEditing(true)}
              variant="primary"
              className="d-inline-flex align-items-center px-3 py-2 fw-medium rounded-3"
            >
              <Edit3 size={16} className="me-2" />
              <span>Edit Profile</span>
            </Button>
          ) : (
            <div className="d-flex gap-2">
              <Button
                onClick={handleSave}
                variant="success"
                className="d-inline-flex align-items-center px-3 py-2 fw-medium rounded-3"
              >
                <Save size={16} className="me-2" />
                <span>Save</span>
              </Button>
              <Button
                onClick={handleCancel}
                variant="secondary"
                className="d-inline-flex align-items-center px-3 py-2 fw-medium rounded-3"
              >
                <X size={16} className="me-2" />
                <span>Cancel</span>
              </Button>
            </div>
          )} */}
        </Col>
      </Row>
 
      <Card className="shadow-sm rounded-3 p-4 mb-4">
        <Card.Header className="bg-white border-0 px-0 pt-0 pb-3">
          <h2 className="fs-5 fw-semibold text-dark mb-0">Personal Information</h2>
        </Card.Header>
        <Row className="g-4">
          <Col md={6}>
            <Form.Group className="mb-0">
              <Form.Label className="small fw-medium text-muted mb-1">Username</Form.Label>
              {/* Always display static text, removed conditional rendering for Form.Control */}
              <div className="d-flex align-items-center bg-light p-3 rounded-3">
                <User size={20} className="text-muted me-3" />
                <span>{driverData.userName || ""}</span>
              </div>
            </Form.Group>
          </Col>
 
          <Col md={6}>
            <Form.Group className="mb-0">
              <Form.Label className="small fw-medium text-muted mb-1">First Name</Form.Label>
              {/* Always display static text */}
              <div className="d-flex align-items-center bg-light p-3 rounded-3">
                <User size={20} className="text-muted me-3" />
                <span>{driverData.firstName || ""}</span>
              </div>
            </Form.Group>
          </Col>
 
          <Col md={6}>
            <Form.Group className="mb-0">
              <Form.Label className="small fw-medium text-muted mb-1">Last Name</Form.Label>
              {/* Always display static text */}
              <div className="d-flex align-items-center bg-light p-3 rounded-3">
                <User size={20} className="text-muted me-3" />
                <span>{driverData.lastName || ""}</span>
              </div>
            </Form.Group>
          </Col>
 
          <Col md={6}>
            <Form.Group className="mb-0">
              <Form.Label className="small fw-medium text-muted mb-1">Email Address</Form.Label>
              {/* Always display static text */}
              <div className="d-flex align-items-center bg-light p-3 rounded-3">
                <Mail size={20} className="text-muted me-3" />
                <span>{driverData.email || ""}</span>
              </div>
            </Form.Group>
          </Col>
 
          <Col md={6}>
            <Form.Group className="mb-0">
              <Form.Label className="small fw-medium text-muted mb-1">Phone Number</Form.Label>
              {/* Always display static text */}
              <div className="d-flex align-items-center bg-light p-3 rounded-3">
                <Phone size={20} className="text-muted me-3" />
                <span>{driverData.phoneNumber || ""}</span>
              </div>
            </Form.Group>
          </Col>
        </Row>
      </Card>
 
      <Card className="shadow-sm rounded-3 p-4 mb-4">
        <Card.Header className="bg-white border-0 px-0 pt-0 pb-3">
          <h2 className="fs-5 fw-semibold text-dark mb-0">Driver Details</h2>
        </Card.Header>
        <Row className="g-4">
          <Col md={6}>
            <Form.Group className="mb-0">
              <Form.Label className="small fw-medium text-muted mb-1">License Number</Form.Label>
              <div className="d-flex align-items-center bg-light p-3 rounded-3">
                <span>{driverData.licenseNumber || ""}</span>
              </div>
            </Form.Group>
          </Col>
 
          <Col md={6}>
            <Form.Group className="mb-0">
              <Form.Label className="small fw-medium text-muted mb-1">Vehicle Model</Form.Label>
              <div className="d-flex align-items-center bg-light p-3 rounded-3">
                <span>{driverData.vehicleModel || ""}</span>
              </div>
            </Form.Group>
          </Col>
 
          <Col md={6}>
            <Form.Group className="mb-0">
              <Form.Label className="small fw-medium text-muted mb-1">License Plate</Form.Label>
              <div className="d-flex align-items-center bg-light p-3 rounded-3">
                <span>{driverData.licensePlate || ""}</span>
              </div>
            </Form.Group>
          </Col>
        </Row>
      </Card>
 
      <Card className="shadow-sm rounded-3 p-4 mb-4">
        <Card.Header className="bg-white border-0 px-0 pt-0 pb-3">
          <h2 className="fs-5 fw-semibold text-dark mb-0">Driver Status</h2>
        </Card.Header>
        <Row className="text-center g-4 align-items-center">
          <Col xs={12} md={4}>
            <div className={`p-3 rounded-3 ${isDriverApproved ? "bg-success-subtle" : "bg-danger-subtle"}`}>
              <div className={`fs-4 fw-bold ${isDriverApproved ? "text-success" : "text-danger"} mb-2 d-flex align-items-center justify-content-center`}>
                {isDriverApproved ? <CheckCircle size={28} className="me-2" /> : <XCircle size={28} className="me-2" />}
                {isDriverApproved ? "Approved" : "Not Approved"}
              </div>
              <div className="small text-muted">Approval Status</div>
            </div>
          </Col>
 
          <Col xs={12} md={4}>
            <div className={`p-3 rounded-3 ${isStatusActive ? "bg-info-subtle" : "bg-warning-subtle"}`}>
              <div className={`fs-4 fw-bold ${isStatusActive ? "text-info" : "text-warning"} mb-2`}>
                {currentDriverStatus}
              </div>
              <div className="small text-muted mb-2">Current Operational Status</div>
              <Dropdown onSelect={handleStatusChange}>
                <Dropdown.Toggle
                  variant="secondary"
                  id="dropdown-status"
                  disabled={!isDriverApproved}
                >
                  Change Status
                </Dropdown.Toggle>
                <Dropdown.Menu>
                  <Dropdown.Item
                    eventKey="Available"
                    disabled={driverData?.status === "ON_RIDE" || !isDriverApproved}
                  >
                    Available
                  </Dropdown.Item>
                  <Dropdown.Item
                    eventKey="Offline"
                    disabled={driverData?.status === "ON_RIDE" || !isDriverApproved}
                  >
                    Offline
                  </Dropdown.Item>
                  {driverData?.status === "ON_RIDE" && (
                    <Dropdown.Item eventKey="On Ride" disabled>
                      On Ride (Cannot Change)
                    </Dropdown.Item>
                  )}
                </Dropdown.Menu>
              </Dropdown>
              {!isDriverApproved && (
                <Alert variant="warning" className="mt-2 p-2 text-center small">
                  Status changes are disabled until approved.
                </Alert>
              )}
            </div>
          </Col>
        </Row>
      </Card>
    </Container>
  );
};
 
export default DriverProfile;