import React, { useState, useContext } from "react";
import { useNavigate, Link } from "react-router-dom";
import { User, Car, Mail, Lock, ArrowLeft } from "lucide-react";
import { Container, Card, Button, Form, InputGroup } from "react-bootstrap";


import { useAuth } from "../context/AuthContext";
import { loginUser } from "../service/authService";

const Login = () => {
  const navigate = useNavigate();
  const {login} = useAuth();
 
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
        const data = await loginUser(formData);
        console.log("Login service response data:", data);
        
        login(data.jwtToken,{username: data.userName, role: data.role, userId:data.entityId});
        
        console.log("Login function in AuthContext called.");
        console.log(localStorage.getItem('token'));
        
        alert("Login successful!");
        navigate("/dashboard");
    } catch (error) {
        console.error(error); 
        alert(error.message || "Login failed"); 
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
            <h2 className="fs-4 fw-semibold text-secondary">Welcome User</h2>
            <p className="text-muted mt-2">Sign in to your account</p>
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
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
                  placeholder="Enter your username"
                  required
                />
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
                  placeholder="Enter your email"
                  required
                />
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
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Enter your password"
                  required
                />
              </InputGroup>
            </Form.Group>

            <Button
              type="submit"
              variant="light"
              className="w-100 py-3 rounded-lg fw-bold"
              style={{
                background: "rgba(241, 211, 2, 1)",
                border: "none",
              }}
            >
              Sign In
            </Button>
          </Form>

          <div className="mt-4 text-center">
            <span className="text-muted">Don't have an account? </span>
            <Link
              to="/register"
              className="text-primary fw-semibold text-decoration-none"
            >
              Sign up here
            </Link>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default Login;
