import React, { useEffect, useState, useCallback } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Car, MapPin, Plus, UserCircle2, IndianRupee } from "lucide-react";
import axios from 'axios';
import { useAuth } from "../context/AuthContext";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Badge,
  ListGroup,
  Spinner,
} from "react-bootstrap";

const Dashboard = () => {
  const [currentUser, setCurrentUser] = useState(null);
  const [rides, setRides] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [paymentLoading, setPaymentLoading] = useState({});
  const { token, user, logout } = useAuth();
  const nav = useNavigate();

  const API_GATEWAY_URL = "http://localhost:8086";

  const fetchPaymentStatus = useCallback(async (rideId) => {
    if (!token) return;

    setPaymentLoading(prev => ({ ...prev, [rideId]: true }));
    try {
      const response = await axios.get(`${API_GATEWAY_URL}/api/v1/payments/ride/${rideId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });
      return response.data.data;
    } catch (paymentError) {
      console.error(`Error fetching payment for ride ${rideId}:`, paymentError);
      if (axios.isAxiosError(paymentError) && paymentError.response && paymentError.response.status === 404) {
        return { status: "NOT_FOUND" };
      }
      return { status: "ERROR" };
    } finally {
      setPaymentLoading(prev => ({ ...prev, [rideId]: false }));
    }
  }, [token]);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      setPaymentLoading({});

      try {
        if (!user || !user.userId) {
          console.error("User info not found or userId missing. Redirecting to login.");
          setLoading(false);
          setError("User not logged in or info missing.");
          return;
        }

        setCurrentUser(user);
        const userId = user.userId;
        console.log("Fetching dashboard data for userId:", userId);

        const USER_RIDES_API_URL = `${API_GATEWAY_URL}/api/v1/users/rides/${userId}`;

        const ridesRes = await axios.get(USER_RIDES_API_URL, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        const fetchedRides = ridesRes.data.data;

        const ridesWithPaymentStatus = await Promise.all(
          fetchedRides.map(async (ride) => {
            if (ride.status === "COMPLETED") {
              const paymentData = await fetchPaymentStatus(ride.id);
              return { ...ride, paymentStatus: paymentData?.status, actualFare: paymentData?.amount || ride.actualFare }; // Also update actualFare from payment if available
            }
            return ride; 
          })
        );

        setRides(ridesWithPaymentStatus);
        setLoading(false);

      } catch (error) {
        console.error("Error fetching dashboard data:", error);
        setLoading(false);

        if (axios.isAxiosError(error) && error.response && error.response.status === 404) {
          console.warn("Rides API returned 404 Not Found. Displaying empty rides gracefully.");
          setRides([]);
          setError(null);
        } else if (error.response) {
          setError(`Error: ${error.response.status} - ${error.response.data?.message || error.message}`);
        } else if (error.request) {
          setError("Network Error: No response from server. Check if backend is running.");
        } else {
          setError(`Request Error: ${error.message}`);
        }
      }
    };

    if (token && user?.userId) {
      fetchData();
    } else {
      setLoading(false);
      if (!token) setError("Not authenticated. Please log in to view dashboard.");
    }
  }, [token, user, fetchPaymentStatus]);

  const handleTokenExpires = () => {
    logout();
    nav("/login");
  }

  if (loading) {
    return (
      <Container className="text-center py-5">
        <Spinner animation="border" role="status" className="mb-2" />
        <p className="text-muted">Loading your dashboard...</p>
      </Container>
    );
  }

  if (error) {
    return (
      <Container className="text-center py-5">
        <h2 className="text-danger">Error Loading Data</h2>
        <p className="text-muted">{error}</p>
        <Button onClick={handleTokenExpires}>Login</Button>
      </Container>
    );
  }

  const sortedRides = [...rides].sort((a, b) => new Date(b.startTime) - new Date(a.startTime));
  const recentRides = sortedRides.slice(0, 3);

  const hasActiveRide = rides.some(ride =>
    ride.status === "SEARCHING_DRIVER" ||
    ride.status === "DRIVER_ASSIGNED" ||
    ride.status === "RIDE_STARTED"
  );

  return (
    <Container className="my-4 py-4"> 
      <Row className="mb-4 justify-content-between align-items-center">
        <Col xs={12} md={8}>
          <h1 className="fs-2 fw-bold text-dark">
            Welcome back, {currentUser?.username || currentUser?.email || "User"}!
          </h1>
          <p className="text-muted mt-1">Ready for your next ride?</p>
        </Col>
        <Col xs={12} md={4} className="text-md-end mt-3 mt-md-0">
          <Link
            to="/book-ride"
            className={`btn btn-primary d-inline-flex align-items-center py-2 px-3 rounded-lg fw-semibold shadow-sm ${hasActiveRide ? 'disabled' : ''}`}
            style={{
              background: "linear-gradient(to right, #2563eb, #6d28d9)",
              border: "none",
              pointerEvents: hasActiveRide ? 'none' : 'auto',
              opacity: hasActiveRide ? 0.6 : 1,
            }}
            aria-disabled={hasActiveRide}
          >
            <Plus size={20} className="me-2" />
            Book New Ride
          </Link>
        </Col>
      </Row>

      <Row className="mb-4 g-4 align-items-stretch">
        <StatCard
          label="Completed"
          value={rides.filter((r) => r.status === "COMPLETED" && r.paymentStatus === "SUCCESS").length}
          icon={<Car size={24} className="text-success" />}
          bg="success"
        />
        <StatCard
          label="Pending Payment"
          value={rides.filter(r => r.status === "COMPLETED" && r.paymentStatus === "PENDING").length}
          icon={<IndianRupee size={24} className="text-info" />}
          bg="info"
        />
        <StatCard
          label="In Progress"
          value={rides.filter(r => r.status === "SEARCHING_DRIVER" || r.status === "DRIVER_ASSIGNED" || r.status === "RIDE_STARTED").length}
          icon={<Car size={24} className="text-warning" />}
          bg="warning"
        />
      </Row>


      <Card className="p-4 shadow-sm rounded-3">
        <Card.Header className="bg-white border-0 px-0 pt-0 pb-3">
          <h2 className="fs-5 fw-semibold text-dark mb-0">Recent Rides</h2>
        </Card.Header>

        {recentRides.length > 0 ? (
          <ListGroup variant="flush">
            {recentRides.map((ride, index) => (
              <ListGroup.Item
                key={ride.id}
                className={`border-bottom border-light p-3`}
              >
                <Row className="align-items-start"> 
                  <Col xs={9}>
                    <RideInfo label="From" value={ride.pickupLocation} />
                    <RideInfo label="To" value={ride.dropoffLocation} />
                    {(ride.status === "DRIVER_ASSIGNED" || ride.status === "RIDE_STARTED" || ride.status === "COMPLETED") && ride.driverFullname && ( //ride infomation
                      <div className="d-flex align-items-center small text-muted mb-2">
                        <UserCircle2 size={16} className="text-primary me-2" />
                        <span>Driver: {ride.driverFullname}</span>
                      </div>
                    )}
                    <div className="d-flex flex-wrap align-items-center small text-muted">
                      <span className="me-3">
                        Date: {new Date(ride.startTime).toLocaleDateString()}
                      </span>
                      {ride.distance !== undefined && ride.distance !== null && (
                        <span className="me-3">
                          Distance: {ride.distance.toFixed(2)} km
                        </span>
                      )}
                      {ride.duration !== undefined && ride.duration !== null && (
                        <span className="me-3">
                          Duration: {ride.duration} mins
                        </span>
                      )}
                      <span>
                        Price: ₹{ride.actualFare ? ride.actualFare.toFixed(2) : '0.00'}
                      </span>
                    </div>
                  </Col>
                  <Col xs={3} className="text-end d-flex flex-column align-items-end justify-content-between">
                    <Badge
                      className={`px-2 py-1 rounded-pill text-uppercase fw-semibold ${
                        ride.status === "COMPLETED"
                          ? (ride.paymentStatus === "PENDING" ? "bg-info text-dark" : "bg-success text-white")
                          : ride.status === "IN_PROGRESS" || ride.status === "RIDE_STARTED" || ride.status === "DRIVER_ASSIGNED" || ride.status === "SEARCHING_DRIVER"
                          ? "bg-warning text-dark"
                          : "bg-danger text-white"
                      }`} //Ride Status 
                    >
                      {ride.status.replace(/_/g, ' ').charAt(0).toUpperCase() +
                        ride.status.replace(/_/g, ' ').slice(1).toLowerCase()}
                      {ride.status === "COMPLETED" && ride.paymentStatus === "PENDING" && " (PENDING PAYMENT)"}
                    </Badge>

                    {paymentLoading[ride.id] ? (
                      <Spinner animation="border" size="sm" className="mt-2" />
                    ) : (
                      ride.status === "COMPLETED" && ride.paymentStatus === "PENDING" && (
                        <Button
                          variant="warning"
                          size="sm"
                          className="mt-2 d-inline-flex align-items-center"
                          onClick={() => nav("/payment", { state: { rideId: ride.id, actualFare: ride.actualFare } })}
                        >
                          <IndianRupee size={16} className="me-1" /> Pay ₹{ride.actualFare ? ride.actualFare.toFixed(2) : '0.00'}
                        </Button>
                      )
                    )}
                  </Col>
                </Row>
              </ListGroup.Item>
            ))}
          </ListGroup>
        ) : (
          <div className="text-center py-5">
            <Car size={64} className="text-muted mx-auto mb-4" />
            <h3 className="fw-semibold text-dark mb-3">No rides found yet!</h3>
            <p className="text-muted mb-4">
              It looks like you haven't booked any rides. Start your first journey today!
            </p>
            <Link
              to="/book-ride"
              className="btn btn-outline-primary d-inline-flex align-items-center py-2 px-4 rounded-lg fw-medium border-2"
            >
              <Plus size={18} className="me-2" />
              Book Your First Ride
            </Link>
          </div>
        )}
      </Card>
    </Container>
  );
};

const StatCard = ({ label, value, icon, bg }) => (
  <Col xs={12} md={6} lg={3}>
    <Card className="p-4 shadow-sm rounded-3 h-100">
      <Card.Body className="d-flex align-items-center justify-content-between p-0">
        <div>
          <p className="small fw-bold text-dark mb-1">{label}</p>
          <p className="fs-4 fw-bold text-dark mb-0">{value}</p>
        </div>
        <div className={`bg-${bg}-subtle p-3 rounded-circle d-flex align-items-center justify-content-center`} style={{ width: '50px', height: '50px' }}>
          {icon}
        </div>
      </Card.Body>
    </Card>
  </Col>
);

const RideInfo = ({ label, value }) => (
  <div className="d-flex align-items-center mb-2">
    <MapPin size={16} className="text-muted me-2" />
    <span className="small text-muted">
      {label}: {value}
    </span>
  </div>
);

export default Dashboard;