import React from "react";
import { Link } from "react-router-dom";
import { Car, Star, Shield, Clock } from "lucide-react";
import { Container, Row, Col, Button, Card } from "react-bootstrap";

const Home = () => {
  return (
    <div
      className="min-vh-100"
      style={{
        background: "linear-gradient(to bottom right, #eff6ff, #e0e7ff)",
      }}
    >
      <div
        className="text-white py-5"
        style={{ background: "rgba(241, 211, 2, 1)" }}
      >
        <Container className="py-5 text-center">
          <div className="d-flex justify-content-center align-items-center mb-4">
            <Car size={48} className="me-3" />
            <h1 className="display-4 fw-bold">ApexRide</h1>
          </div>
          <p className="lead mb-4">Your reliable ride booking companion</p>
          <p
            className="fs-5 mb-5 opacity-80 mx-auto"
            style={{ maxWidth: "40rem" }}
          >
            Connect with professional drivers, book rides instantly, and travel
            safely to your destination.
          </p>

          <div className="d-flex flex-column flex-sm-row gap-3 justify-content-center">

            <Button
              as={Link}
              to="/driver-register"
              variant="light"
              className="text-primary px-4 py-3 rounded-lg fw-bold shadow"
              style={{ minWidth: "150px" }}
            >
              Drive with Us
            </Button>

          </div>
        </Container>
      </div>

      <div className="py-5">
        <Container className="py-5 text-center">
          <h2 className="display-6 fw-bold text-dark mb-3">
            Why Choose ApexRide?
          </h2>
          <p
            className="fs-5 text-secondary mx-auto"
            style={{ maxWidth: "40rem" }}
          >
            Experience the best in ride booking with our premium features
          </p>
          <Row className="g-4 justify-content-center mt-4">
            <Col md={4}>
              <Card className="p-4 shadow-lg h-100">
                <div
                  className="bg-primary-subtle rounded-circle d-flex align-items-center justify-content-center mb-4 mx-auto"
                  style={{ width: "64px", height: "64px" }}
                >
                  <Clock size={32} className="text-primary" />
                </div>
                <h3 className="fs-5 fw-semibold mb-3 text-left">
                  Quick Booking
                </h3>
                <Card.Text className="text-secondary">
                  Book your ride in seconds with our intuitive interface.
                  Available drivers nearby respond instantly.
                </Card.Text>
              </Card>
            </Col>
            <Col md={4}>
              <Card className="p-4 shadow-lg h-100">
                <div
                  className="bg-success-subtle rounded-circle d-flex align-items-center justify-content-center mb-4 mx-auto"
                  style={{ width: "64px", height: "64px" }}
                >
                  <Shield size={32} className="text-success" />
                </div>
                <h3 className="fs-5 fw-semibold mb-3">Safe & Secure</h3>
                <Card.Text className="text-secondary">
                  All drivers are verified and vetted. Real-time tracking and
                  secure payment options included.
                </Card.Text>
              </Card>
            </Col>
            <Col md={4}>
              <Card className="p-4 shadow-lg h-100">
                <div
                  className="rounded-circle d-flex align-items-center justify-content-center mb-4 mx-auto"
                  style={{
                    width: "64px",
                    height: "64px",
                    backgroundColor: "#ede9fe",
                  }}
                >
                  <Star size={32} style={{ color: "#9333ea" }} />
                </div>
                <h3 className="fs-5 fw-semibold mb-3">Highly Rated</h3>
                <Card.Text className="text-secondary">
                  Rate and review your rides. Our community of drivers maintains
                  the highest service standards.
                </Card.Text>
              </Card>
            </Col>
          </Row>
        </Container>
      </div>

      

      <div
        className="text-white py-5"
        style={{ background: "linear-gradient(to right, #6d28d9, #2563eb)" }}
      >
        <Container className="py-4 text-center">
          <h2 className="display-6 fw-bold mb-3">Ready to Get Started?</h2>
          <p className="lead mb-4 opacity-90">
            Join thousands of satisfied customers today
          </p>
          <div className="d-flex flex-column flex-sm-row gap-3 justify-content-center">
            <Button
              as={Link}
              to="/login"
              variant="light"
              className="px-4 py-3 rounded-lg fw-bold"
              style={{ color: "#9333ea", minWidth: "150px" }}
            >
              Sign In
            </Button>
            <Button
              as={Link}
              to="/driver-register"
              variant="light"
              className="px-4 py-3 rounded-lg fw-bold"
              style={{ color: "#9333ea", minWidth: "150px" }}
            >
              Create Account
            </Button>
          </div>
        </Container>
      </div>
    </div>
  );
};

export default Home;
