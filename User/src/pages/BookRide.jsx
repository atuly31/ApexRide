import React, { useState } from "react";
import { MapPin, Navigation, Calculator, XCircle, Info } from "lucide-react"; 
import {
  Container,
  Card,
  Button,
  Form,
  InputGroup,
  ListGroup,
  Modal,
  ButtonGroup,
} from "react-bootstrap";
import MapPreview from "../components/MapPreview";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
import axios from "axios";

const rideTypes = [
  { id: "economy", label: "Eco", multiplier: 1 },    
  { id: "premium", label: "Prem", multiplier: 1.5 }, 
  { id: "luxury", label: "Lux", multiplier: 2 },    
];

const BookRide = () => {
  const { token, user, isBookRideDisabled } = useAuth();
  const navigate = useNavigate();

  const [pickup, setPickup] = useState("");
  const [destination, setDestination] = useState("");
  const [pickupCoords, setPickupCoords] = useState(null);
  const [destinationCoords, setDestinationCoords] = useState(null);
  const [rideType, setRideType] = useState("economy"); 
  const [actualFare, setActualFare] = useState(0);
  const [distance, setDistance] = useState(0); 
  const [duration, setDuration] = useState(0); 
  const [showEstimate, setShowEstimate] = useState(false);
  const [showErrorModal, setShowErrorModal] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  const handleShowError = (message) => {
    setErrorMessage(message);
    setShowErrorModal(true);
  };

  const handleCloseError = () => setShowErrorModal(false);

  const handleShowSuccess = (message) => {
    setSuccessMessage(message);
    setShowSuccessModal(true);
  };

  const handleCloseSuccess = () => {
    setShowSuccessModal(false);
    navigate("/dashboard");
  };

  const geocodeLocation = async (location) => {
    try {
      const res = await fetch(
        `https://nominatim.openstreetmap.org/search?format=json&q=${location}`
      );
      const data = await res.json();
      if (data && data.length > 0) {
        return { lat: parseFloat(data[0].lat), lng: parseFloat(data[0].lon) };
      }
      return null;
    } catch (error) {
      console.error("Geocoding error:", error);
      handleShowError("Failed to geocode location. Please try again.");
      return null;
    }
  };

  const handleCalculate = async () => {
    if (!pickup || !destination) {
      handleShowError("Please enter both pickup and destination.");
      return;
    }

    setShowEstimate(false);
    setDistance(0);
    setDuration(0);
    setActualFare(0);

    const pickupLoc = await geocodeLocation(pickup);
    const destinationLoc = await geocodeLocation(destination);

    if (!pickupLoc || !destinationLoc) {
      return;
    }

    setPickupCoords(pickupLoc);
    setDestinationCoords(destinationLoc);
  };

  const handleRouteInfo = ({ distance, time }) => {
    const baseRate = 2.5; 
    const selectedRideType = rideTypes.find((type) => type.id === rideType);
    const fare =
      Math.round(
        distance * baseRate * (selectedRideType?.multiplier || 1) * 100
      ) / 100;

    setDistance(parseFloat(distance));
    setDuration(time);
    setActualFare(fare);
    setShowEstimate(true);
    console.log("Calculated Route Info:");
    console.log("Distance:", distance, "km");
    console.log("Duration:", time, "minutes");
    console.log("Estimated Fare (before type multiplier):", (distance * baseRate).toFixed(2));
    console.log("Ride Type Multiplier:", selectedRideType?.multiplier || 1);
    console.log("Final Estimated Fare: ₹", fare.toFixed(2));
  };

  const handleBookRide = async () => {
    if (!user || !user.userId || !token) {
      handleShowError("You must be logged in to book a ride.");
      alert("Token has Expired Please Log in again")
      navigate("/login");
      return;
    }

    if (!pickup || !destination || isNaN(actualFare) || actualFare <= 0 || isNaN(distance) || distance <= 0 || isNaN(duration) || duration <= 0) {
      handleShowError("Please calculate a valid fare, distance, and duration first before booking.");
      return;
    }

    const userId = user.userId;
    const BOOK_RIDE_API_URL = `http://localhost:8086/api/v1/users/book-ride/${userId}`;

    const requestPayload = {
      pickupLocation: pickup,
      dropoffLocation: destination,
      duration: duration,
      actualFare: parseFloat(actualFare.toFixed(2)),
      distance: parseFloat(distance.toFixed(2)),
    };

    console.log("Booking Ride Request Payload:", requestPayload);
    console.log("Booking Ride API URL:", BOOK_RIDE_API_URL);
    console.log("Auth Token:", token ? token.substring(0, 10) + "..." : "No token");

    try {
      const response = await axios.post(BOOK_RIDE_API_URL, requestPayload, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      console.log("Ride Booking API Response:", response.data);
      handleShowSuccess("Ride booked successfully!");
      setPickup("");
      setDestination("");
      setPickupCoords(null);
      setDestinationCoords(null);
      setActualFare(0);
      setDistance(0);
      setDuration(0);
      setShowEstimate(false);
      setRideType("economy"); 
    } catch (error) {
      console.error("Error booking ride:", error);
      if (error.response) {
        if (error.response.status === 401 || error.response.status === 403) {
            handleShowError("Authentication required. Please log in again.");
            navigate("/login");
        } else {
            handleShowError(`Booking failed: ${error.response.data?.message || error.message}.`);
        }
      } else if (error.request) {
        handleShowError("Network error: No response from server. Please check your internet connection or try again later.");
      } else {
        handleShowError(`An unexpected error occurred: ${error.message}.`);
      }
    }
  };

  if (isBookRideDisabled) {
    return (
      <Container className="my-5 py-5 text-center">
        <Card className="p-4 shadow-sm mx-auto" style={{ maxWidth: '600px' }}>
          <Card.Body>
            <XCircle size={64} className="text-danger mb-4" />
            <h2 className="mb-3 text-dark">Cannot Book Ride Now!</h2>
            <p className="text-muted lead mb-4">
              It looks like you have an active ride in progress or your last ride was not completed.
              Please complete your current journey or resolve the previous one before booking another.
            </p>
            <Button variant="primary" onClick={() => window.location.reload()} className="mt-3">
              Refresh Status
            </Button>
            <Button variant="outline-secondary" href="/dashboard" className="mt-3 ms-2">
              Go to Dashboard
            </Button>
          </Card.Body>
        </Card>
      </Container>
    );
  }

  return (
    <Container className="my-4 py-4" style={{ maxWidth: "800px" }}>
      <Card className="p-4 shadow-lg rounded-4 mb-4">
        <Card.Body>
          <h1 className="fs-2 fw-bold text-dark mb-4">Book Your Ride</h1>

          <Form className="mb-4">
            <Form.Group className="mb-3" controlId="pickupLocation">
              <Form.Label>Pickup Location</Form.Label>
              <InputGroup>
                <InputGroup.Text>
                  <MapPin size={20} />
                </InputGroup.Text>
                <Form.Control
                  type="text"
                  value={pickup}
                  onChange={(e) => setPickup(e.target.value)}
                  placeholder="Enter pickup location"
                />
              </InputGroup>
            </Form.Group>

            <Form.Group className="mb-3" controlId="destination">
              <Form.Label>Destination</Form.Label>
              <InputGroup>
                <InputGroup.Text>
                  <MapPin size={20} />
                </InputGroup.Text>
                <Form.Control
                  type="text"
                  value={destination}
                  onChange={(e) => setDestination(e.target.value)}
                  placeholder="Enter destination"
                />
              </InputGroup>
            </Form.Group>

            <Form.Group className="mb-4" controlId="rideType"> 
              <Form.Label>Ride Type</Form.Label>
              <ButtonGroup className="d-flex rounded-pill overflow-hidden shadow-sm" style={{ backgroundColor: '#e9ecef' }}>
                {rideTypes.map((type) => (
                  <Button
                    key={type.id}
                    variant={rideType === type.id ? 'dark' : 'light'}
                    onClick={() => setRideType(type.id)}
                    className="flex-grow-1 py-2 px-3 fw-medium border-0"
                    style={{ transition: 'background-color 0.2s, color 0.2s' }}
                  >
                    {type.label}
                  </Button>
                ))}
              </ButtonGroup>
            </Form.Group>
          

          </Form>

          <MapPreview
            pickup={pickupCoords}
            drop={destinationCoords}
            setRouteInfo={handleRouteInfo}
            key={`${pickupCoords?.lat}-${pickupCoords?.lng}-${destinationCoords?.lat}-${destinationCoords?.lng}`}
          />

          {!showEstimate && (
            <Button
              onClick={handleCalculate}
              variant="primary"
              className="w-100 mt-3"
            >
              <Calculator size={20} className="me-2" />
              Calculate Fare
            </Button>
          )}

          {showEstimate && (
            <>
              <Card className="bg-light p-3 mb-4 border-0 mt-3">
                <Card.Header className="bg-transparent border-bottom-0 ps-0 pe-0 pt-0 pb-2">
                  <h3 className="fw-semibold text-dark mb-0">Fare Estimate</h3>
                </Card.Header>
                <ListGroup variant="flush">
                  <ListGroup.Item className="d-flex justify-content-between">
                    <span>Distance:</span>
                    <span>{distance.toFixed(2)} km</span>
                  </ListGroup.Item>
                  <ListGroup.Item className="d-flex justify-content-between">
                    <span>Estimated Time:</span>
                    <span>{duration} mins</span>
                  </ListGroup.Item>
                  <ListGroup.Item className="d-flex justify-content-between">
                    <span>Ride Type:</span>
                    {/* Display the full label for the estimate, not the short one */}
                    <span>{rideTypes.find(t => t.id === rideType)?.label || rideType}</span>
                  </ListGroup.Item>
                  <ListGroup.Item className="d-flex justify-content-between fw-bold">
                    <span>Total Fare:</span>
                    <span>₹{actualFare.toFixed(2)}</span>
                  </ListGroup.Item>
                </ListGroup>
              </Card>

              <Button
                onClick={handleBookRide}
                variant="success"
                className="w-100"
              >
                <Navigation size={20} className="me-2" />
                Book Ride - ₹{actualFare.toFixed(2)}
              </Button>

              <Button
                onClick={() => {
                  setPickup("");
                  setDestination("");
                  setPickupCoords(null);
                  setDestinationCoords(null);
                  setActualFare(0);
                  setDistance(0);
                  setDuration(0);
                  setShowEstimate(false);
                  setRideType("economy"); // Reset to default ride type
                }}
                variant="outline-secondary"
                className="w-100 mt-2"
              >
                Reset Form
              </Button>
            </>
          )}
        </Card.Body>
      </Card>

      {/* Error Modal */}
      <Modal show={showErrorModal} onHide={handleCloseError} centered>
        <Modal.Header closeButton>
          <Modal.Title className="text-danger">Error</Modal.Title>
        </Modal.Header>
        <Modal.Body>{errorMessage}</Modal.Body>
        <Modal.Footer>
          <Button variant="danger" onClick={handleCloseError}>
            Close
          </Button>
        </Modal.Footer>
      </Modal>

      {/* Success Modal */}
      <Modal show={showSuccessModal} onHide={handleCloseSuccess} centered>
        <Modal.Header closeButton>
          <Modal.Title className="text-success">Success!</Modal.Title>
        </Modal.Header>
        <Modal.Body>{successMessage}</Modal.Body>
        <Modal.Footer>
          <Button variant="success" onClick={handleCloseSuccess}>
            OK
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default BookRide;