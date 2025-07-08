import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Car, User, Mail, Phone, Lock, ArrowLeft } from "lucide-react";
import { Container, Card, Button, Form, InputGroup } from "react-bootstrap";
import registerService from "../service/registerService";

const Register = () => {
  const [formData, setFormData] = useState({
    userName: "",
    firstName: "",
    lastName: "",
    email: "",
    phoneNumber: "",
    passwordHash: "",
  });

  const [errors, setErrors] = useState({}); 

  const navigate = useNavigate(); 

  
  const validateField = (name, value) => {
    let error = "";
    switch (name) {
      case "userName":
        if (!value.trim()) {
          error = "Username is required.";
        } else if (value.trim().length < 3) { 
          error = "Username must be at least 3 characters long.";
        }
        break;
      case "firstName":
        if (!value.trim()) {
          error = "First name is required.";
        }
        break;
      case "lastName":
        if (!value.trim()) {
          error = "Last name is required.";
        }
        break;
      case "email":
        if (!value.trim()) {
          error = "Email is required.";
        } else if (!/\S+@\S+\.\S+/.test(value)) {
          error = "Email address is invalid.";
        }
        break;
      case "phoneNumber":
        if (!value.trim()) {
          error = "Phone number is required.";
        } else if (!/^\d{10}$/.test(value)) {
          
          error = "Phone number must be 10 digits.";
        }
        break;
      case "passwordHash":
        if (value.length < 6) {
          error = "Password must be at least 6 characters long.";
        } else if (!/[0-9]/.test(value)) {
          error = "Password must contain at least one digit.";
        } else if (!/[a-zA-Z]/.test(value)) {
          error = "Password must contain at least one letter.";
        }
        break;
      default:
        break;
    }
    return error;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
    // Validate the field immediately as the user types
    const error = validateField(name, value);
    setErrors((prevErrors) => ({
      ...prevErrors,
      [name]: error,
    }));
  };

  const handleBlur = (e) => {
    const { name, value } = e.target;
  
    const error = validateField(name, value);
    setErrors((prevErrors) => ({
      ...prevErrors,
      [name]: error,
    }));
  };

  const validateFormOnSubmit = () => {
    let newErrors = {};
    let isValid = true;

   
    for (const key in formData) {
      const error = validateField(key, formData[key]);
      if (error) {
        newErrors[key] = error;
        isValid = false;
      }
    }

    setErrors(newErrors);
    return isValid;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (validateFormOnSubmit()) {
      try {
        const response = await registerService.registerUser(formData);
        console.log("Registration successful:", response);
        alert("Registration successful! You can now log in.");
        navigate("/login"); 
      } catch (error) {
        console.error("Registration failed:", error);
        alert(
          "Registration failed. Please try again. " +
            (error.response?.data?.message || error.message)
        );
      }
    } else {
      console.log("Form validation failed.");
    }
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
              <Car size={32} className="text-primary me-2" />
              <h1 className="fs-2 fw-bold text-dark">ApexRide</h1>
            </div>
            <h2 className="fs-4 fw-semibold text-secondary">Create Account</h2>
            <p className="text-muted mt-2">Join as a user</p>
          </div>

          <Form onSubmit={handleSubmit}>
            <Form.Group className="mb-3">
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
                  onBlur={handleBlur}
                  placeholder="Create a username"
                  isInvalid={!!errors.userName}
                  required
                />
                <Form.Control.Feedback type="invalid">
                  {errors.userName}
                </Form.Control.Feedback>
              </InputGroup>
            </Form.Group>

            <Form.Group className="mb-3">
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
                  onBlur={handleBlur}
                  placeholder="Enter your first name"
                  isInvalid={!!errors.firstName}
                  required
                />
                <Form.Control.Feedback type="invalid">
                  {errors.firstName}
                </Form.Control.Feedback>
              </InputGroup>
            </Form.Group>

            <Form.Group className="mb-3">
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
                  onBlur={handleBlur}
                  placeholder="Enter your last name"
                  isInvalid={!!errors.lastName}
                  required
                />
                <Form.Control.Feedback type="invalid">
                  {errors.lastName}
                </Form.Control.Feedback>
              </InputGroup>
            </Form.Group>

            <Form.Group className="mb-3">
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
                  onBlur={handleBlur}
                  placeholder="Enter your email"
                  isInvalid={!!errors.email}
                  required
                />
                <Form.Control.Feedback type="invalid">
                  {errors.email}
                </Form.Control.Feedback>
              </InputGroup>
            </Form.Group>

            <Form.Group className="mb-3">
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
                  onBlur={handleBlur}
                  placeholder="Enter your phone number"
                  isInvalid={!!errors.phoneNumber}
                  required
                />
                <Form.Control.Feedback type="invalid">
                  {errors.phoneNumber}
                </Form.Control.Feedback>
              </InputGroup>
            </Form.Group>

            <Form.Group className="mb-4">
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
                  onBlur={handleBlur}
                  placeholder="Create a password"
                  isInvalid={!!errors.passwordHash}
                  required
                />
                <Form.Control.Feedback type="invalid">
                  {errors.passwordHash}
                </Form.Control.Feedback>
              </InputGroup>
            </Form.Group>

            <Button
              type="submit"
              className="text-dark w-100 py-3 rounded-lg fw-bold"
              style={{ background: "rgba(241, 211, 2, 1)", border: "none" }}
            >
              Create Account
            </Button>
          </Form>

          <div className="mt-4 text-center">
            <span className="text-muted">Already have an account? </span>
            <Link
              to="/login"
              className="text-primary fw-semibold text-decoration-none"
            >
              Sign In
            </Link>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default Register;