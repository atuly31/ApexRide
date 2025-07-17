import React, { useCallback, useEffect, useState } from "react";
import { Container, Card, Button, Form, Row, Col } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import {findUserRideLatestRide} from "../service/authService";
import { submitRating } from "../service/authService";
import { useAuth } from "../context/AuthContext";
import { useNavigate } from "react-router-dom";
const RatingPage = () => {
  const [rating, setRating] = useState(0);
  const [hover, setHover] = useState(0);
  const [comment, setComment] = useState("");
  const [rideHistory, setRideHistory] = useState({});
  
  const {user,token} = useAuth(); 
  const nav = useNavigate();
  const fetchUserRideHistory = useCallback(async ()=>{
    try {
        const response = await findUserRideLatestRide(user.userId,token);
        console.log(response)
       
        setRideHistory(response);
       
        return response;
        
      } catch (error) {
        console.error("", error);
       
      }

  })

  const handleSubmit = async() => {

    if (rating === 0 && comment.trim() === "") {
      alert("Please provide a rating or a comment before submitting.");
      return;
    }
    try {
        const response = await submitRating(rideHistory.id, rating, token);
        console.log("Rating submission response:", response);
        alert("Thank you for your feedback!");
        nav("/dashboard");

    } catch (error) {
        console.error("Failed to submit rating:", error.message);
        alert(`Error submitting rating: ${error.message}`);
      }
    console.log(rating);
 
    console.log(rideHistory);
   
    console.log("Submitted Rating:", rating);
    console.log("Comment:", comment);

    setComment("");
    setRating(0);
    setHover(0);
  };
  const capitalizeFirstLetter = (str) => {
    if (!str) return '';
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
  };
  
  useEffect(()=>{
    fetchUserRideHistory();
   },[])

  return (
    <Container className="my-3 d-flex justify-content-center">
      <Card className="p-3 shadow-sm" style={{ maxWidth: '500px', width: '100%' }}>
        <Card.Header className="text-center bg-light py-2">
          <Card.Title as="h5" className="mb-1">Ride Ended</Card.Title>
          <Card.Text className="text-muted mb-0">Distance Driven</Card.Text>
          <h4 className="fw-bold mb-0">
            4.2 <span className="text-dark fs-6">KM</span>
          </h4>
        </Card.Header>

        <Card.Body className="text-center">
          <h6 className="mb-2 text-dark">
            How was your ride with <span className="text-primary"></span>?
          </h6>

          <div className="d-flex justify-content-center align-items-center mb-3">
            {[...Array(5)].map((_, index) => {
              const starValue = index + 1;
              return (
                <span
                  key={starValue}
                  style={{
                    fontSize: "1.8rem",
                    cursor: "pointer",
                    color: starValue <= (hover || rating) ? "#f4c542" : "#ccc",
                    transition: "color 0.2s ease-in-out",
                  }}
                  onClick={() => setRating(starValue)}
                  onMouseEnter={() => setHover(starValue)}
                  onMouseLeave={() => setHover(rating)}
                >
                  ★
                </span>
              );
            })}
            <span className="ms-2 fs-6 fw-bold text-muted">{hover || rating}/5</span>
          </div>

          <Form.Group className="mb-3">
            <Form.Label className="text-start fw-bold text-dark" style={{ fontSize: '0.9rem' }}>
              Leave a Comment (Optional)
            </Form.Label>
            <Form.Control
              as="textarea"
              rows={2}
              placeholder="Share your experience..."
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              className="shadow-sm"
              style={{ fontSize: '0.9rem' }}
            />
          </Form.Group>

          <Button 
            variant="warning" 
            className="w-100 py-1 fw-bold text-dark" 
            onClick={handleSubmit}
            style={{ fontSize: '0.9rem' }}
          >
            Submit Feedback
          </Button>

          <hr className="my-3" />

          <div className="text-start">
            <h6 className="mb-2 text-dark">Ride Details</h6>
            <Row className="mb-1">
              <Col xs={4} className="fw-bold text-muted">Pickup:</Col>
              <Col xs={8}>{capitalizeFirstLetter(rideHistory.pickupLocation)}</Col>
            </Row>
            <Row>
              <Col xs={4} className="fw-bold text-muted">Drop:</Col>
              <Col xs={8}>{capitalizeFirstLetter(rideHistory.dropoffLocation)}</Col>
            </Row>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default RatingPage;
