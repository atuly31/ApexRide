import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  Car,
  User,
  Mail,
  Phone,
  Lock,
  FileText,
  ArrowLeft,
  CarFront,
} from "lucide-react";
import { Container, Card, Button, Form, InputGroup } from "react-bootstrap";
import registerService from "../service/registerService";

const DriverRegister = () => {
  const [formData, setFormData] = useState({
    userName: "",
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    licenseNumber: "",
    vehicleModel: "",
    licensePlate: "",
    passwordHash: "",
  });

  const [passwordError, setPasswordError] = useState("");
  const nav = useNavigate();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));

    if (name === "passwordHash") {
      validatePassword(value);
    }
  };

  const validatePassword = (password) => {
    let error = "";
    if (password.length < 6) {
      error = "Password must be at least 6 characters long.";
    } else if (!/[0-9]/.test(password)) {
      error = "Password must contain at least one digit.";
    } else if (!/[a-zA-Z]/.test(password)) {
      error = "Password must contain at least one letter.";
    }
    setPasswordError(error);
    return error === ""; // Returns true if valid, false otherwise
  };

  const handleSubmit = async(e) => {
    e.preventDefault();

    const isPasswordValid = validatePassword(formData.passwordHash);

    if (!isPasswordValid) {
      // If password is not valid, stop the submission
      alert("Please fix the password errors before submitting.");
      return;
    }
    try {
      const response = await registerService.registerDriver(formData);
      console.log("Registration successful:", response);
      alert("Registration successful! You can now log in .");
      nav("/login");
    } catch (error) {
      console.error("Registration failed:", error);
      alert("Registration failed. Please try again. " + (error.response?.data || error.message));
    }

    console.log("Driver Registration Data:", formData);

    setFormData({
      userName: "",
      firstName: "",
      lastName: "",
      email: "",
      phoneNumber: "",
      licenseNumber: "",
      vehicleModel: "",
      licensePlate: "",
      passwordHash: "",
    });
  };

  return (
    <Container
      fluid
      className="d-flex justify-content-center align-items-center min-vh-100 py-4"
      style={{
        background: "linear-gradient(to bottom right, #eff6ff, #e0e7ff)",
      }}
    >
      <Card
        className="p-4 shadow-lg rounded-4"
        style={{ maxWidth: "500px", width: "100%" }}
      >
        <Card.Body>
          <Link
            to="/"
            className="d-inline-flex align-items-center text-primary text-decoration-none mb-4"
          >
            <ArrowLeft size={16} className="me-2" />
            Back to Home
          </Link>

          <div className="text-center mb-4">
            <div className="d-flex justify-content-center align-items-center mb-3">
              <Car size={32} style={{ color: "#9333ea" }} className="me-2" />
              <h1 className="fs-2 fw-bold text-dark">ApexRide</h1>
            </div>
            <h2 className="fs-4 fw-semibold text-secondary">Join as Driver</h2>
            <p className="text-muted mt-2">Start earning with us today</p>
          </div>

          <Form onSubmit={handleSubmit}>
            {/* Username */}
            <Form.Group className="mb-3" controlId="driverUsername">
              <Form.Label className="small fw-medium text-dark mb-2">
                Username
              </Form.Label>
              <InputGroup>
                <InputGroup.Text>
                  <User size={20} className="text-muted" />
                </InputGroup.Text>
                <Form.Control
                  type="text"
                  name="userName"
                  value={formData.userName}
                  onChange={handleChange}
                  placeholder="Create a username"
                  required
                />
              </InputGroup>
            </Form.Group>

            {/* First Name */}
            <Form.Group className="mb-3" controlId="driverFirstName">
              <Form.Label className="small fw-medium text-dark mb-2">
                First Name
              </Form.Label>
              <InputGroup>
                <InputGroup.Text>
                  <User size={20} className="text-muted" />
                </InputGroup.Text>
                <Form.Control
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  placeholder="Enter your first name"
                  required
                />
              </InputGroup>
            </Form.Group>

            {/* Last Name */}
            <Form.Group className="mb-3" controlId="driverLastName">
              <Form.Label className="small fw-medium text-dark mb-2">
                Last Name
              </Form.Label>
              <InputGroup>
                <InputGroup.Text>
                  <User size={20} className="text-muted" />
                </InputGroup.Text>
                <Form.Control
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  placeholder="Enter your last name"
                  required
                />
              </InputGroup>
            </Form.Group>

            {/* Email */}
            <Form.Group className="mb-3" controlId="driverEmail">
              <Form.Label className="small fw-medium text-dark mb-2">
                Email Address
              </Form.Label>
              <InputGroup>
                <InputGroup.Text>
                  <Mail size={20} className="text-muted" />
                </InputGroup.Text>
                <Form.Control
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="Enter your email"
                  required
                />
              </InputGroup>
            </Form.Group>

            {/* Phone */}
            <Form.Group className="mb-3" controlId="driverPhone">
              <Form.Label className="small fw-medium text-dark mb-2">
                Phone Number
              </Form.Label>
              <InputGroup>
                <InputGroup.Text>
                  <Phone size={20} className="text-muted" />
                </InputGroup.Text>
                <Form.Control
                  type="tel"
                  name="phoneNumber"
                  value={formData.phoneNumber}
                  onChange={handleChange}
                  placeholder="Enter your phone number"
                  required
                />
              </InputGroup>
            </Form.Group>

            {/* License Number */}
            <Form.Group className="mb-3" controlId="driverLicenseNumber">
              <Form.Label className="small fw-medium text-dark mb-2">
                License Number
              </Form.Label>
              <InputGroup>
                <InputGroup.Text>
                  <FileText size={20} className="text-muted" />
                </InputGroup.Text>
                <Form.Control
                  type="text"
                  name="licenseNumber"
                  value={formData.licenseNumber}
                  onChange={handleChange}
                  placeholder="Enter your license number"
                  required
                />
              </InputGroup>
            </Form.Group>

            <Form.Group className="mb-3" controlId="driverVehicleModel">
              <Form.Label className="small fw-medium text-dark mb-2">
                Vehicle Model
              </Form.Label>
              <InputGroup>
                <InputGroup.Text>
                  <Car size={20} className="text-muted" />
                </InputGroup.Text>
                <Form.Control
                  type="text"
                  name="vehicleModel"
                  value={formData.vehicleModel}
                  onChange={handleChange}
                  placeholder="e.g., Toyota Camry"
                  required
                />
              </InputGroup>
            </Form.Group>

            {/* License Plate */}
            <Form.Group className="mb-3" controlId="driverlicensePlate">
              <Form.Label className="small fw-medium text-dark mb-2">
                License Plate
              </Form.Label>
              <InputGroup>
                <InputGroup.Text>
                  <CarFront size={20} className="text-muted" />
                </InputGroup.Text>
                <Form.Control
                  type="text"
                  name="licensePlate"
                  value={formData.licensePlate}
                  onChange={handleChange}
                  placeholder="e.g., ABC-123"
                  required
                />
              </InputGroup>
            </Form.Group>

            {/* Password */}
            <Form.Group className="mb-4" controlId="driverPassword">
              <Form.Label className="small fw-medium text-dark mb-2">
                Password
              </Form.Label>
              <InputGroup>
                <InputGroup.Text>
                  <Lock size={20} className="text-muted" />
                </InputGroup.Text>
                <Form.Control
                  type="password"
                  name="passwordHash"
                  value={formData.passwordHash}
                  onChange={handleChange}
                  placeholder="Create a password"
                  required
                  isInvalid={!!passwordError} 
                />
                <Form.Control.Feedback type="invalid">
                  {passwordError}
                </Form.Control.Feedback>
              </InputGroup>
            </Form.Group>

            <Button
              type="submit"
              variant="primary"
              className="w-100 py-3 rounded-lg text-dark fw-bold"
              style={{
                background: "rgba(241, 211, 2, 1)",
                border: "none",
              }}
            >
              Join as Driver
            </Button>
          </Form>

          <div className="mt-4 text-center">
            <span className="text-muted">Already have an account? </span>
            <Link
              to="/login"
              className="fw-semibold text-decoration-none"
              style={{ color: "#9333ea" }}
            >
              Sign In
            </Link>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default DriverRegister;