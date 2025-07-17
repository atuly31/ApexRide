import React from "react";
import { NavLink, Link } from "react-router-dom";
import { UserCheck, Car } from "lucide-react";
import { Nav, Navbar, Container } from "react-bootstrap";

const Layout = ({ children }) => {
  return (
    <div className="d-flex flex-column min-vh-100 bg-light">
      {/* Top Navbar */}
      <Navbar bg="white" expand="lg" className="shadow border-bottom px-3">
        <Container fluid className="d-flex align-items-center">
          <Navbar.Brand
            as={Link}
            to="/"
            className="d-flex align-items-center"
          >
            <Car size={32} className="text-warning me-2" />
            <span className="fs-5 fw-bold text-warning">ApexRide</span>
          </Navbar.Brand>

          <span className="fs-6 fw-semibold text-dark ms-auto me-3 d-none d-lg-block"></span>

          <Navbar.Toggle aria-controls="responsive-navbar-nav" />

          <Navbar.Collapse id="responsive-navbar-nav">
            <Nav className="flex-column d-lg-none w-100 mt-3 border-top pt-3">
              <ul className="list-unstyled d-grid gap-2">
                <li>
                  <Nav.Link
                    as={NavLink}
                    to="/"
                    className="d-flex align-items-center px-3 py-2 rounded-lg transition-all text-dark hover-bg-light"
                  >
                    <UserCheck size={20} className="me-3" />
                    <span>Admin Dashboard</span>
                  </Nav.Link>
                </li>
              </ul>
            </Nav>
          </Navbar.Collapse>
        </Container>
      </Navbar>

      {/* Sidebar + Main Content */}
      <div className="d-flex flex-grow-1">
        {/* Desktop Sidebar Navigation - Hidden on small screens (d-none) */}
        <Nav
          className="flex-column bg-white shadow-lg p-3 d-none d-lg-flex"
          style={{ width: "250px", minHeight: "100vh" }}
        >
          <ul className="list-unstyled d-grid gap-2">
            <li>
              <Nav.Link
                as={NavLink}
                to="/"
                className="d-flex align-items-center px-3 py-2 rounded-lg transition-all text-dark hover-bg-light"
              >
                <UserCheck size={20} className="me-3" />
                <span>Admin Dashboard</span>
              </Nav.Link>
            </li>
          </ul>
        </Nav>


        <main className="flex-grow-1" style={{ overflowX: "auto", padding: "1rem" }}>
          {children}
        </main>
      </div>
    </div>
  );
};

export default Layout;