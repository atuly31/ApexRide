import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Car, Mail, Lock, User, ArrowLeft } from "lucide-react";
import { Container, Card, Button, Form, InputGroup } from "react-bootstrap";
import { useAuth } from "../context/AuthContext";
import { loginDriver } from "../service/authService";
const DriverLogin = () => {
    const nav = useNavigate();
    const {login} = useAuth();

    const [formData, setFormData] = useState(
      {
        username:"",
        email:"",
        password:""
      }
    )
    const handleChange = (e)=>{
      const {name,value} = e.target;
      setFormData((preData)=>({
        ...preData,
        [name]:value,
      }))
    }

    const handleSubmit = async (e) => {
      e.preventDefault();
      try {
          const data = await loginDriver(formData);
          console.log("Login service response data:", data);
          
          login(data.jwtToken,{username: data.userName, role: data.role, userId:data.entityId});
          
          console.log("Login function in AuthContext called.");
          console.log("Token in localStorage immediately after setToken (via AuthContext):", localStorage.getItem('token'));
          
          alert("Login successful!");
          nav("/driver-dashboard");
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
            <h2 className="fs-4 fw-semibold text-secondary">Welcome Driver</h2>
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
              variant="primary"
              className="w-100 py-3 rounded-lg fw-semibold"
              style={{
                background: "linear-gradient(to right, #2563eb, #6d28d9)",
                border: "none",
              }}
            >
              Sign In
            </Button>
          </Form>

          <div className="mt-4 text-center">
            <span className="text-muted">Don't have an account? </span>
            <Link
              to="/driver-register"
              className="fw-semibold text-decoration-none"
              style={{ color: "#9333ea" }}
            >
              Join as Driver
            </Link>
          </div>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default DriverLogin;
