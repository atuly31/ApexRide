import React from 'react';
import { NavLink, Link, useNavigate } from 'react-router-dom';
import { Car, User, LogOut, Home, History, Settings } from 'lucide-react'; // History icon is still imported but not used
import { Container, Navbar, Nav, Button, Badge } from 'react-bootstrap';
import { useAuth } from '../context/AuthContext';
// Removed: import '../../src/index.css'; // No longer needed as CSS is embedded
 
const Layout = ({ children }) => {
  const {driver,logout} = useAuth();
  const nav = useNavigate();
  const handleLogout = ()=>{
    logout();
    nav("/login")
  }
  return (
    <div className="d-flex flex-column min-vh-100">
      {/* Embedded CSS for active link styling and mobile menu alignment */}
      <style>
        {`
        /* Custom styles for the active navigation link */
        .nav-link.active {
          background-color: #FFEB3B !important; /* A bright yellow from your screenshot */
          color: #212529 !important; /* Dark text for contrast */
          font-weight: bold !important; /* Make the text bold */
          border-radius: 0.5rem !important; /* Ensure rounded corners are maintained */
        }
 
        /* Ensure icons within the active link also turn dark */
        .nav-link.active svg {
          color: #212529 !important; /* Dark color for icons when active */
        }
 
        /* Default styling for non-active links to ensure consistency */
        .nav-link {
            color: #495057; /* Default text color for inactive links */
            display: flex; /* Ensure flex properties are maintained */
            align-items: center; /* Align items vertically */
            padding: 0.5rem 1rem; /* Adjust padding as needed */
            text-decoration: none; /* Remove default underline */
            transition: all 0.2s ease-in-out; /* Smooth transition for hover effects */
        }
 
        .nav-link svg {
            color: #6c757d; /* Default color for icons when inactive */
            transition: all 0.2s ease-in-out; /* Smooth transition for icon color */
        }
 
        /* Hover effect for non-active links */
        .hover-bg-light:hover {
          background-color: #f8f9fa; /* Light grey on hover */
          color: #212529 !important; /* Dark text on hover */
        }
 
        /* Ensure hover doesn't override active state */
        .nav-link.active.hover-bg-light:hover {
            background-color: #FFEB3B !important; /* Keep active color on hover if it's active */
        }
 
        /* Styles for mobile menu items to align left */
        @media (max-width: 991.98px) { /* Target 'lg' breakpoint and below */
          #responsive-navbar-nav {
            background-color: white; /* Ensure background when expanded */
            padding-bottom: 1rem; /* Add some padding at the bottom */
            border-bottom-left-radius: 0.5rem; /* Rounded corners for expanded menu */
            border-bottom-right-radius: 0.5rem;
          }
 
          /* Force the main Nav inside collapse to be column and left-aligned */
          #responsive-navbar-nav .navbar-nav { /* Target the direct Bootstrap Nav component */
            flex-direction: column !important; /* Stack items vertically */
            align-items: flex-start !important; /* Align all items to the left */
            width: 100% !important; /* Take full width */
            margin-left: 0 !important; /* Remove any auto margins */
            margin-right: 0 !important; /* Remove any auto margins */
          }
 
          /* Target the username div specifically for left alignment and bottom padding */
          #responsive-navbar-nav .navbar-nav .d-flex.align-items-center.me-3.my-2.my-lg-0 {
            width: 100%; /* Take full width */
            justify-content: flex-start !important; /* Ensure content inside is left-aligned */
            padding-left: 1rem; /* Add some padding for visual separation */
            margin-right: 0 !important; /* Remove me-3 */
            margin-bottom: 0.5rem !important; /* Added bottom margin here */
          }
          /* Target the logout button specifically for left alignment */
          #responsive-navbar-nav .navbar-nav .btn.btn-link {
            width: 100%; /* Take full width */
            text-align: left !important; /* Align button text to the left */
            padding-left: 1rem !important; /* Add some padding for visual separation */
          }
 
          /* Adjust generic margins for mobile items to prevent unwanted spacing */
          #responsive-navbar-nav .navbar-nav .my-2 {
            margin-top: 0.5rem !important;
            margin-bottom: 0.5rem !important;
          }
          #responsive-navbar-nav .navbar-nav .my-lg-0 {
            margin-top: 0 !important;
            margin-bottom: 0 !important;
          }
         
          /* Add some top margin to the desktop-only username/logout group when it's part of the mobile menu */
          #responsive-navbar-nav .navbar-nav:first-child {
            margin-top: 1rem !important;
          }
        }
        `}
      </style>
 
      <Navbar bg="white" expand="lg" className="shadow border-bottom">
        <Container fluid>
 
          {/* for brand icon, title and link to home and some style */}
          <Navbar.Brand as={Link} to="/" className="d-flex align-items-center">
            <Car size={32} className="text-warning me-2" />
            <span className="fs-5 fw-bold text-warning">ApexRide</span>
          </Navbar.Brand>
 
          {/* for toggle buttons on smaller screen */}
          <Navbar.Toggle />
 
{/* It is hidden on large screens by d-lg-none Hidden by default, appears on toggle click, full width */}

          <Navbar.Collapse id="responsive-navbar-nav"> 
            <Nav className="align-items-center ms-auto mb-2 mb-lg-0">
              <div className="d-flex align-items-center me-3 my-2 my-lg-0">
                <User size={25} className="text-secondary me-1" />
                <span className="small text-muted me-2">{driver?.username || "Guest"}</span>
              </div>
 
              <Button variant="link" className="text-decoration-none text-muted"
                onClick={handleLogout}
              >
                <LogOut size={16} className="me-1" />
                <span>Logout</span>
              </Button>
            </Nav>
 
            <Nav className="flex-column d-lg-none w-100 mt-3 border-top pt-3">
              <ul className="list-unstyled d-grid gap-2">
                <li>
                  <Nav.Link as={NavLink} to="/driver-dashboard" className="d-flex align-items-center px-3 py-2 rounded-lg transition-all text-dark hover-bg-light">
                    <Home size={20} className="me-3" />
                    <span>Dashboard</span>
                  </Nav.Link>
                </li>
               
                <li>
                  <Nav.Link as={NavLink} to="/driver-profile" className="d-flex align-items-center px-3 py-2 rounded-lg transition-all text-dark hover-bg-light">
                    <Settings size={20} className="me-3" />
                    <span>Profile</span>
                  </Nav.Link>
                </li>
                {/* Removed Ride History from here for mobile sidebar */}
              </ul>
            </Nav>
 
          </Navbar.Collapse>
        </Container>
      </Navbar>
 
      <div className="d-flex flex-grow-1">
        {/* Desktop Sidebar Navigation - Hidden on small screens (d-none) */}
        <Nav className="flex-column bg-white shadow-lg p-3 d-none d-lg-flex" style={{ width: '250px', minHeight: 'calc(100vh - 64px)' }}>
          <ul className="list-unstyled d-grid gap-2">
            <li>
              <Nav.Link as={NavLink} to="/driver-dashboard" className="d-flex align-items-center px-3 py-2 rounded-lg transition-all text-dark hover-bg-light">
                <Home size={20} className="me-3" />
                <span>Dashboard</span>
              </Nav.Link>
            </li>
           
            <li>
              <Nav.Link as={NavLink} to="/driver-profile" className="d-flex align-items-center px-3 py-2 rounded-lg transition-all text-dark hover-bg-light">
                <Settings size={20} className="me-3" />
                <span>Profile</span>
              </Nav.Link>
            </li>
            {/* Removed Ride History from here for desktop sidebar */}
          </ul>
        </Nav>
 
        <main className="flex-grow-1 p-4">
          {children}
        </main>
      </div>
    </div>
  );
};
 
export default Layout;
 