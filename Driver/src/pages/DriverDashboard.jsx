import React, { useState, useEffect, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import {
  Car,
  IndianRupee,
  Star,
  Clock,
  MapPin,
  CheckCircle,
  XCircle,
  Info,
  User,
} from "lucide-react";
import { acceptRide, completeRide } from "../service/authService";

import {
  Container,
  Row,
  Col,
  Card,
  Badge,
  ListGroup,
  Button,
  Spinner,
  Alert,
} from "react-bootstrap";
import axios from "axios";
import { useAuth } from "../context/AuthContext";

const DriverDashboard = () => {
  const { driver, token, logout } = useAuth();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [driverProfile, setDriverProfile] = useState(null);
  const [pendingRides, setPendingRides] = useState([]);
  const [completedRides, setCompletedRides] = useState([]);
  const [totalEarnings, setTotalEarnings] = useState(0);
  const [averageRating, setAverageRating] = useState(0); // State for average rating

  const [paymentLoading, setPaymentLoading] = useState({});

  const API_GATEWAY_URL = "http://localhost:8086";

  const fetchPaymentDetails = useCallback(
    async (rideId) => {
      if (!token) return;

      setPaymentLoading((prev) => ({ ...prev, [rideId]: true }));
      try {
        const response = await axios.get(
          `${API_GATEWAY_URL}/api/v1/payments/ride/${rideId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        return response.data.data; // Returns payment object with status and amount
      } catch (paymentError) {
        console.error(`Error fetching payment for ride ${rideId}:`, paymentError);
        if (
          axios.isAxiosError(paymentError) &&
          paymentError.response &&
          paymentError.response.status === 404
        ) {
          return { status: "NOT_FOUND" }; // Indicate no payment record found
        }
        return { status: "ERROR" }; // Indicate a general error
      } finally {
        setPaymentLoading((prev) => ({ ...prev, [rideId]: false }));
      }
    },
    [token]
  );

  const fetchData = useCallback(async () => {
    setLoading(true);
    setError(null);
    setPaymentLoading({});
    try {
      if (!driver || !driver.userId) {
        console.error(
          "Driver info not found or userId missing. Redirecting to login."
        );
        setLoading(false);
        setError("Driver not logged in or info missing.");
        logout();
        navigate("/login");
        return;
      }

      const driverId = driver.userId;
      console.log("Fetching dashboard data for driverId:", driverId);

      const DRIVER_PROFILE_API_URL = `${API_GATEWAY_URL}/api/v1/drivers/${driverId}`; //calls the driver MircroService
      console.log("Fetching driver profile from:", DRIVER_PROFILE_API_URL);
      const profileRes = await axios.get(DRIVER_PROFILE_API_URL, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      const profileData = profileRes.data.data;
      setDriverProfile(profileData);
      console.log("Driver Profile Data received:", profileData);

      //Fetch Driver Ride History
      const DRIVER_RIDES_API_URL = `${API_GATEWAY_URL}/api/v1/drivers/rideHistory/${driverId}`; //ride mircoservice
      console.log("Fetching driver ride history from:", DRIVER_RIDES_API_URL);

      let ridesData = [];
      try {
        const ridesRes = await axios.get(DRIVER_RIDES_API_URL, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        ridesData = ridesRes.data.data;
        console.log("Extracted Rides Data received:", ridesData);
      } catch (ridesError) {
        if (
          axios.isAxiosError(ridesError) &&
          ridesError.response &&
          ridesError.response.status === 404
        ) {
          console.warn(
            "Rides API returned 404 Not Found. Displaying empty rides gracefully."
          );
          ridesData = [];
        } else {
          console.error("Error fetching ride history:", ridesError);
          setError(
            `Failed to load ride history: ${
              ridesError.response?.data?.message || ridesError.message
            }`
          );
        }
      }

      // filter for pending rides to include RIDE_STARTED
      const pending = ridesData.filter(
        (ride) =>
          ride.status === "DRIVER_ASSIGNED" || ride.status === "RIDE_STARTED" // Keep RIDE_STARTED in pending
      );

      //  Process completed rides to fetch payment status
      const completed = await Promise.all(
        ridesData
          .filter((ride) => ride.status === "COMPLETED")
          .map(async (ride) => {
            const paymentData = await fetchPaymentDetails(ride.id);
            return {
              ...ride,
              paymentStatus: paymentData?.status,
              actualFare: paymentData?.amount || ride.actualFare,
            };
          })
      );

      setPendingRides(pending);
      setCompletedRides(completed);

      setTotalEarnings(
        completed.reduce((sum, ride) => {
          if (ride.status === "COMPLETED" && ride.paymentStatus === "SUCCESS") {
            return sum + (ride.actualFare || 0);
          }
          return sum;
        }, 0)
      );

      // Calculate average rating
      const totalRatings = completed.filter(ride => ride.rating != null);
      if (totalRatings.length > 0) {
        const sumRatings = totalRatings.reduce((sum, ride) => sum + ride.rating, 0);
        setAverageRating(sumRatings / totalRatings.length);
      } else {
        setAverageRating(0);      
      }

    } catch (mainError) {
      console.error(
        "Error fetching driver dashboard data (main catch):",
        mainError
      );
      if (axios.isAxiosError(mainError)) {
        if (
          mainError.response.status === 401 ||
          mainError.response.status === 403
        ) {
          alert("Session expired or unauthorized. Please log in again.");
          logout();
          navigate("/login");
        } else {
          setError(
            `Error: ${mainError.response.status} - ${
              mainError.response.data?.message ||
              mainError.response.statusText ||
              "Server Error"
            }`
          );
        }
      } else if (mainError.request) {
        setError(
          "Network Error: No response from server. Check if backend is running."
        );
      } else {
        setError(`Request Setup Error: ${mainError.message}`);
      }
    } finally {
      setLoading(false);
    }
  }, [driver, token, logout, navigate, fetchPaymentDetails]);

  useEffect(() => {
    if (token) {
      fetchData();
    } else {
      setLoading(false);
      setError("Not authenticated. Please log in to view dashboard.");
    }
  }, [token, driver, fetchData, navigate]);

  const handleAcceptRide = useCallback(async () => {
    console.log(
      "Attempting to accept ride (backend handles ride selection based on driverId)."
    );
    try {
      const response = await acceptRide(driver.userId, token);
      console.log("Ride accepted successfully:", response);
      alert("Ride accepted! Check your 'Available Ride Requests'.");
      fetchData();
    } catch (error) {
      console.error("Failed to accept ride:", error);
      alert(`Failed to accept ride: ${error.message || "Unknown error"}`);
    }
  }, [driver, token, fetchData]);

  const handleCompleteRide = useCallback(
    async (rideId) => {
      console.log("Attempting to complete ride with ID:", rideId);
      try {
        const response = await completeRide(rideId, driver.userId, token);
        console.log("Ride completed successfully:", response);
        alert("Ride completed! Waiting for user payment.");

        fetchData();
      } catch (error) {
        console.error("Failed to complete ride:", error);
        alert(`Failed to complete ride: ${error.message || "Unknown error"}`);
      }
    },
    [driver, token, fetchData]
  ); 

  
  let displayOverallStatus = "";
  let statusBadgeVariant = "";
  let statusIcon = null; 

  if (driverProfile) {
    if (driverProfile.approved === false) {
      displayOverallStatus = "Not Approved";
      statusBadgeVariant = "danger";
      statusIcon = <XCircle size={24} className="me-2" />;
    } else {
      switch (driverProfile.status) {
        case "AVAILABLE":
          displayOverallStatus = "Available";
          statusBadgeVariant = "success";
          statusIcon = <CheckCircle size={24} className="me-2" />;
          break;
        case "ON_RIDE":
          displayOverallStatus = "On Ride";
          statusBadgeVariant = "warning";
          statusIcon = <Car size={24} className="me-2" />;
          break;
        case "OFFLINE":
          displayOverallStatus = "Offline";
          statusBadgeVariant = "secondary";
          statusIcon = <Info size={24} className="me-2" />;
          break;
        default:
          displayOverallStatus = "Unknown";
          statusBadgeVariant = "secondary";
          statusIcon = <Info size={24} className="me-2" />;
          break;
      }
    }
  } else {
    displayOverallStatus = "Loading...";
    statusBadgeVariant = "secondary";
    statusIcon = <Info size={24} className="me-2" />;
  }

  const totalRidesCount = completedRides.length + pendingRides.length; 

  if (loading) {
    return (
      <Container className="text-center py-5">
        <Spinner animation="border" role="status" className="mb-3">
          <span className="visually-hidden">Loading your dashboard...</span>
        </Spinner>
        <p className="text-muted">Loading your driver dashboard...</p>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="text-center py-5">
        <Alert variant="danger">
          <Alert.Heading>Error Loading Data!</Alert.Heading>
          <p>{error}</p>
          <hr />
          <Button onClick={fetchData} className="me-2">
            Retry
          </Button>{" "}
          <Button variant="secondary" onClick={() => navigate("/login")}>
            Go to Login
          </Button>
        </Alert>
      </Container>
    );
  }

  if (!driverProfile) {
    return (
      <Container className="text-center py-5">
        <Alert variant="info">
          <Alert.Heading>Profile Not Found</Alert.Heading>
          <p>Could not load driver profile. Please try logging in again.</p>
          <hr />
          <Button onClick={() => navigate("/login")}>Login</Button>
        </Alert>
      </Container>
    );
  }

  return (
    <Container className="my-4 py-4">
      <Row className="mb-4 d-flex justify-content-between align-items-center">
        <Col xs={12} md={8}>
          <h1 className="fs-2 fw-bold text-dark">
            Welcome back, {driverProfile?.userName || "Driver"}!
          </h1>
          <p className="text-muted mt-1">Ready for your next ride?</p>
        </Col>
        <Col xs={12} md={4} className="text-md-end mt-3 mt-md-0">
          <div
            className={`d-inline-flex align-items-center justify-content-center px-4 py-2 rounded-pill fs-6 fw-semibold bg-${statusBadgeVariant}-subtle text-${statusBadgeVariant}`}
          >
            {statusIcon}
            {displayOverallStatus}
          </div>
        </Col>
      </Row>

      
      {driverProfile.approved === false && (
        <Alert variant="warning" className="mb-4 text-center">
          <Info size={20} className="me-2" /> Your account is **Not Approved** yet. You cannot accept or complete rides until your profile is reviewed and approved by the administration.
        </Alert>
      )}

      
      <Row className="mb-4 g-4">
      
        <Col xs={12} md={6} lg={4}>
          <Card className="p-4 shadow-sm rounded-3">
            <Card.Body className="d-flex align-items-center justify-content-between p-0">
              <div>
                <p className="small fw-medium text-bold mb-1">
                  Total Earnings (Paid)
                </p>
                <p className="fs-4 fw-bold text-success mb-0">
                  ₹{totalEarnings.toFixed(2)}
                </p>
              </div>
              <div className="bg-success-subtle p-3 rounded-circle">
                <IndianRupee size={24} className="text-success" />
              </div>
            </Card.Body>
          </Card>
        </Col>

        {/* Total Rides */}
        <Col xs={12} md={6} lg={4}>
          <Card className="p-4 shadow-sm rounded-3">
            <Card.Body className="d-flex align-items-center justify-content-between p-0">
              <div>
                <p className="small fw-medium text-bold mb-1">Total Rides</p>
                <p className="fs-4 fw-bold text-primary mb-0">
                  {totalRidesCount}
                </p>
              </div>
              <div className="bg-primary-subtle p-3 rounded-circle">
                <Car size={24} className="text-primary" />
              </div>
            </Card.Body>
          </Card>
        </Col>

        {/* Rating */}
        <Col xs={12} md={6} lg={4}>
          <Card className="p-4 shadow-sm rounded-3">
            <Card.Body className="d-flex align-items-center justify-content-between p-0">
              <div>
                <p className="small fw-medium text-bold mb-1">Overall Rating</p>
                <p className="fs-4 fw-bold text-info mb-0">
                  {averageRating.toFixed(1)}
                </p>
              </div>
              <div className="bg-info-subtle p-3 rounded-circle">
                <Star size={24} className="text-info" />
              </div>
            </Card.Body>
          </Card>
        </Col>
      </Row>

      <Card className="p-4 shadow-sm rounded-3 mb-4">
        <Card.Header className="bg-white border-0 px-0 pt-0 pb-3">
          <h2 className="fs-5 fw-semibold text-dark mb-0">
            Vehicle Information
          </h2>
        </Card.Header>
        <Row className="g-4">
          <Col md={6}>
            <div className="mb-3">
              <div className="d-flex justify-content-between">
                <span className="text-muted">Vehicle Model:</span>
                <span className="fw-medium text-dark">
                  {driverProfile?.vehicleModel || "N/A"}
                </span>
              </div>
            </div>
            <div className="mb-3">
              <div className="d-flex justify-content-between">
                <span className="text-muted">License Plate:</span>
                <span className="fw-medium text-dark">
                  {driverProfile?.licensePlate || "N/A"}
                </span>
              </div>
            </div>
          </Col>
          <Col md={6}>
            <div className="mb-3">
              <div className="d-flex justify-content-between">
                <span className="text-muted">License Number:</span>
                <span className="fw-medium text-dark">
                  {driverProfile?.licenseNumber || "N/A"}
                </span>
              </div>
            </div>
            {/* Displaying approval status here */}
            <div className="mb-3">
              <div className="d-flex justify-content-between">
                <span className="text-muted">Approval Status:</span>
                <span
                  className={`fw-medium text-${driverProfile.approved ? 'success' : 'danger'}`}
                >
                  {driverProfile.approved ? 'Approved' : 'Not Approved'}
                </span>
              </div>
            </div>
          </Col>
        </Row>
      </Card>

      <Card className="p-4 shadow-sm rounded-3 mb-4">
        <Card.Header className="bg-white border-0 px-0 pt-0 pb-3">
          <h2 className="fs-5 fw-semibold text-dark mb-0">
            Available Ride Requests
          </h2>
        </Card.Header>
        {pendingRides.length > 0 ? (
          <ListGroup variant="flush">
            {pendingRides.map((ride) => (
              <ListGroup.Item
                key={ride.id}
                className="border-bottom border-light p-3 hover-bg-light transition-colors"
              >
                <Row className="align-items-start">
                  <Col xs={9}>
                    <div className="d-flex align-items-center mb-2">
                      <MapPin size={16} className="text-success me-2" />
                      <span className="small text-muted">
                        From: {ride.pickupLocation}
                      </span>
                    </div>
                    <div className="d-flex align-items-center mb-2">
                      <MapPin size={16} className="text-danger me-2" />
                      <span className="small text-muted">
                        To: {ride.dropoffLocation}
                      </span>
                    </div>
                    <div className="d-flex flex-wrap align-items-center small text-muted">
                      {ride.distance && (
                        <span className="me-3">{ride.distance} km</span>
                      )}
                      <span className="me-3">
                        ₹{(ride.actualFare || 0).toFixed(2)}
                      </span>
                      <span>
                        {new Date(ride.startTime).toLocaleTimeString([], {
                          hour: "2-digit",
                          minute: "2-digit",
                        })}
                      </span>
                    </div>
                    <div className="mt-2">
                      <Badge
                        className={`px-2 py-1 rounded-pill text-uppercase fw-semibold ${
                          ride.status === "DRIVER_ASSIGNED"
                            ? "bg-warning text-dark"
                            : ride.status === "RIDE_STARTED" // Corrected: changed from DRIVER_STARTED
                            ? "bg-primary text-white"
                            : "bg-secondary text-white"
                        }`}
                      >
                        {ride.status.replace(/_/g, " ").toUpperCase()}
                      </Badge>
                    </div>
                  </Col>
                  <Col xs={3} className="text-end">
                    {ride.status === "DRIVER_ASSIGNED" && driverProfile.approved && (
                      <Button
                        variant="success"
                        size="sm"
                        className="d-inline-flex align-items-center py-2 px-3 rounded-lg fw-medium"
                        onClick={handleAcceptRide}
                        disabled={!driverProfile.approved}
                      >
                        <CheckCircle size={16} className="me-2" />
                        <span>Accept</span>
                      </Button>
                    )}
                    {ride.status === "RIDE_STARTED" && driverProfile.approved && ( // Corrected: changed from DRIVER_STARTED
                      <Button
                        variant="info"
                        size="sm"
                        className="d-inline-flex align-items-center py-2 px-3 rounded-lg fw-medium"
                        onClick={() => handleCompleteRide(ride.id)}
                        disabled={!driverProfile.approved} // Disable if not approved
                      >
                        <CheckCircle size={16} className="me-2" />
                        <span>Complete Ride</span>
                      </Button>
                    )}
                    {/* Disable buttons if not approved */}
                    {!driverProfile.approved && (
                      <Button
                        variant="secondary"
                        size="sm"
                        className="d-inline-flex align-items-center py-2 px-3 rounded-lg fw-medium"
                        disabled
                      >
                        <XCircle size={16} className="me-2" />
                        <span>Not Approved</span>
                      </Button>
                    )}
                  </Col>
                </Row>
              </ListGroup.Item>
            ))}
          </ListGroup>
        ) : (
          <div className="text-center py-5">
            <Car size={48} className="text-muted mx-auto mb-3" />
            <p className="text-muted">
              No ride requests available at the moment.
            </p>
            <p className="small text-muted mt-2">
              Keep your status as "Available" to receive ride requests.
            </p>
          </div>
        )}
      </Card>

      <Card className="p-4 shadow-sm rounded-3">
        <Card.Header className="bg-white border-0 px-0 pt-0 pb-3">
          <h2 className="fs-5 fw-semibold text-dark mb-0">
            Recent Completed Rides
          </h2>
        </Card.Header>
        {completedRides.length > 0 ? (
          <ListGroup variant="flush">
            {completedRides
              .sort((a, b) => new Date(b.endTime) - new Date(a.endTime))
              .slice(0, 3)
              .map((ride) => (
                <ListGroup.Item
                  key={ride.id}
                  className="border-bottom border-light p-3"
                >
                  <Row className="align-items-start">
                    <Col xs={9}>
                      <div className="d-flex align-items-center mb-2">
                        {paymentLoading[ride.id] ? (
                          <Spinner animation="border" size="sm" className="me-2" />
                        ) : (
                          <Badge
                            className={`px-2 py-1 rounded-pill text-uppercase fw-semibold me-2 ${
                              ride.paymentStatus === "SUCCESS"
                                ? "bg-success text-white"
                                : (ride.status === "COMPLETED" && (ride.paymentStatus === "PENDING" || ride.paymentStatus === "NOT_FOUND"))
                                ? "bg-info text-dark"
                                : "bg-danger text-white"
                            }`}
                          >
                            {ride.status.replace(/_/g, " ").toUpperCase()}
                            {ride.status === "COMPLETED" && ride.paymentStatus === "SUCCESS" && " (PAID)"}
                            {ride.status === "COMPLETED" && (ride.paymentStatus === "PENDING" || ride.paymentStatus === "NOT_FOUND") && " (Payment Pending)"}
                            {ride.status === "COMPLETED" && ride.paymentStatus === "ERROR" && " (PAYMENT ERROR)"}
                          </Badge>
                        )}
                        <span className="small text-muted">
                          {new Date(ride.endTime).toLocaleDateString()}{" "}
                        </span>
                      </div>
                      <div className="mb-2">
                        <div className="d-flex align-items-center mb-1">
                          <MapPin size={16} className="text-success me-2" />
                          <span className="small text-muted">
                            {ride.pickupLocation}
                          </span>
                        </div>
                        <div className="d-flex align-items-center">
                          <MapPin size={16} className="text-danger me-2" />
                          <span className="small text-muted">
                            {ride.dropoffLocation}
                          </span>
                        </div>
                      </div>
                      <div className="d-flex flex-wrap align-items-center small text-muted">
                        {ride.distance && (
                          <span className="me-3">{ride.distance} km</span>
                        )}
                        {ride.duration && (
                          <span className="me-3">{ride.duration} mins</span>
                        )}
                        <span>₹{(ride.actualFare || 0).toFixed(2)}</span>
                      </div>
                    </Col>
                    <Col xs={3} className="text-end">
                      {ride.rating != null && ( // Display rating if available
                        <div className="d-flex align-items-center justify-content-end">
                          <Star size={16} className="text-info me-1" />
                          <span className="small text-muted">
                            {ride.rating.toFixed(1)}
                          </span>
                        </div>
                      )}
                    </Col>
                  </Row>
                </ListGroup.Item>
              ))}
          </ListGroup>
        ) : (
          <div className="text-center py-5">
            <Clock size={48} className="text-muted mx-auto mb-3" />
            <p className="text-muted">No rides completed yet.</p>
            <p className="small text-muted mt-2">
              Complete rides to see them here.
            </p>
          </div>
        )}
      </Card>
    </Container>
  );
};

export default DriverDashboard;