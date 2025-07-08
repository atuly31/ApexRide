import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";
import { useAuth } from "../context/AuthContext";
import { Container, Card, Button, ListGroup, Alert, Spinner } from "react-bootstrap";
import { IndianRupee, ArrowLeft } from "lucide-react";
import PaymentSuccess from "./PaymentSuccess";
const Payment = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { token, user } = useAuth();
 
  const [rideDetails, setRideDetails] = useState(null);
  const [loading, setLoading] = useState(true);
  const [confirmingPayment, setConfirmingPayment] = useState(false);
  const [paymentConfirmed, setPaymentConfirmed] = useState(false);
  const [error, setError] = useState(null);
 
  const rideId = location.state?.rideId;
 
  useEffect(() => {
    if (!rideId || !user?.userId) {
      setError("Missing ride or user information.");
      setLoading(false);
      return;
    }
 
    const fetchRideDetails = async () => {
      try {
        const API_GATEWAY_URL = "http://localhost:8086";
        const response = await axios.get(`${API_GATEWAY_URL}/api/v1/rides/users/${user.userId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
 
        const ride = response.data.find((r) => r.id === rideId);
        if (!ride) {
          setError("Ride not found.");
        } else {
          setRideDetails(ride);
        }
      } catch (err) {
        console.error("Failed to fetch ride details:", err);
        setError("Failed to load ride details.");
      } finally {
        setLoading(false);
      }
    };
 
    fetchRideDetails();
  }, [rideId, user?.userId, token]);
 
  const handleConfirmPayment = async () => {
    setConfirmingPayment(true);
    setError(null);
 
    try {
      const API_GATEWAY_URL = "http://localhost:8086";
      const PAYMENT_CONFIRM_API_URL = `${API_GATEWAY_URL}/api/v1/payments/confirm/${user.userId}`;
      const requestBody = {
        rideId: rideDetails.id,
        amount: rideDetails.actualFare || rideDetails.estimatedFare,
        status: "SUCCESS",
      };
 
      const response = await axios.put(PAYMENT_CONFIRM_API_URL, requestBody, {
        headers: { Authorization: `Bearer ${token}` },
      });
 
      if (response.status === 200) {
        setPaymentConfirmed(true);
        setTimeout(() => {
          navigate("/rating");
        }, 3000); 
      } else {
      
        setError("Unexpected response from payment API.");
        setConfirmingPayment(false); 
      }
      
    } catch (err) {
      console.error("Payment confirmation failed:", err);
      setError("Payment confirmation failed. Please try again.");
      setConfirmingPayment(false);
    }
  };
 
  const convertDurationToMinutes = (duration) => {
    if (!duration) return null;
    const seconds = Number(duration);
    if (isNaN(seconds)) return null;
    return Math.floor(seconds / 60);
  };
 
  if (loading) {
    return (
      <Container className="text-center py-5">
        <Spinner animation="border" />
        <p>Loading ride details...</p>
      </Container>
    );
  }
 
  if (error || !rideDetails) {
    return (
      <Container className="text-center py-5">
        <Alert variant="danger">
          <h4 className="alert-heading">Error</h4>
          <p>{error || "Ride details not found."}</p>
          <Button onClick={() => navigate("/dashboard")}>Go to Dashboard</Button>
        </Alert>
      </Container>
    );
  }

  if (paymentConfirmed) {
    return <PaymentSuccess rideId={rideDetails?.id} />;
  }
 
  return (
    <Container className="py-4" style={{ maxWidth: "700px" }}>
      <Button
        variant="link"
        className="d-inline-flex align-items-center text-primary text-decoration-none mb-3 ps-0"
        onClick={() => navigate(-1)}
        disabled={confirmingPayment}
      >
        <ArrowLeft size={16} className="me-2" />
        Back to Dashboard
      </Button>
 
      <Card className="shadow-sm rounded-3 p-4 mb-4">
        <Card.Body>
          <h2 className="fs-5 fw-semibold text-dark mb-4">Ride Summary</h2>
          <ListGroup variant="flush">
            <ListGroup.Item className="d-flex justify-content-between px-0 py-2">
              <span className="text-muted">From:</span>
              <span className="fw-medium text-dark">{rideDetails.pickupLocation}</span>
            </ListGroup.Item>
            <ListGroup.Item className="d-flex justify-content-between px-0 py-2">
              <span className="text-muted">To:</span>
              <span className="fw-medium text-dark">{rideDetails.dropoffLocation}</span>
            </ListGroup.Item>
            <ListGroup.Item className="d-flex justify-content-between px-0 py-2">
              <span className="text-muted">Distance:</span>
              <span className="fw-medium text-dark">
                {rideDetails.distance?.toFixed(2) || "N/A"} km
              </span>
            </ListGroup.Item>
            {/* <ListGroup.Item className="d-flex justify-content-between px-0 py-2">
              <span className="text-muted">Duration:</span>
              <span className="fw-medium text-dark">
                {convertDurationToMinutes(rideDetails.duration) || "N/A"} mins
              </span>
            </ListGroup.Item> */}
            <ListGroup.Item className="border-top pt-3 d-flex justify-content-between px-0 py-2">
              <span className="fs-5 fw-bold text-dark">Total Amount:</span>
              <span className="fs-5 fw-bold text-success">
                ₹{(rideDetails.actualFare || rideDetails.estimatedFare)?.toFixed(2) || "0.00"}
              </span>
            </ListGroup.Item>
          </ListGroup>
        </Card.Body>
      </Card>
 
      <Card className="shadow-sm rounded-3 p-4 mb-4">
        <Card.Body>
          <h2 className="fs-5 fw-semibold text-dark mb-4">Payment Method</h2>
 
         {error && (
            <Alert variant="danger" className="mb-3 text-center">
              {error}
            </Alert>
          )}
          {paymentConfirmed && (
            <Alert variant="success" className="mb-3 text-center">
              Payment Confirmed! Redirecting...
            </Alert>
          )}
 
          <Button
            onClick={handleConfirmPayment}
            disabled={confirmingPayment || paymentConfirmed}
            className="w-100 shadow py-3 text-dark fw-bold"
            style={{ background: "rgba(241, 211, 2, 1)", border: "none" }}
          >
            {confirmingPayment
              ? "Processing Payment..."
              : paymentConfirmed
              ? "Payment Submitted!"
              : `Pay Now ₹${(rideDetails.actualFare || rideDetails.estimatedFare)?.toFixed(2) || "0.00"} in Cash`}
          </Button>
        </Card.Body>
      </Card>
    </Container>
  );
};
 
export default Payment;