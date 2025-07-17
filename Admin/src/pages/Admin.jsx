import React, { useState, useEffect } from "react";
import {
  Container,
  Row,
  Col,
  Card,
  Button,
  Table,
  Form,
  Alert,
  Spinner,
} from "react-bootstrap";
import { CheckCircle, XCircle } from "lucide-react";
import axios from "axios";

const Admin = () => {
  const [pendingDrivers, setPendingDrivers] = useState([]);
  const [comments, setComments] = useState({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [message, setMessage] = useState(null);

  const ADMIN_ID = "admin123";
  const BASE_URL = "http://localhost:8087/api/v1/admin";

  console.log("Current pendingDrivers state:", pendingDrivers);

  const fetchPendingDrivers = async () => {
    setLoading(true);
    setError(null);
    setMessage(null);
    try {
      const response = await axios.get(`${BASE_URL}/unapproved`);
      setPendingDrivers(response.data);
      console.log("Fetched pending drivers:", response.data);
    } catch (err) {
      console.error("Error fetching pending drivers:", err);
      let errorMessage = "Failed to fetch pending drivers. Please try again.";
      if (err.response && err.response.data && err.response.data.message) {
        errorMessage += ` Reason: ${err.response.data.message}`;
      } else if (err.message) {
        errorMessage += ` Error: ${err.message}`;
      }
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPendingDrivers();
  }, []);

  const handleCommentChange = (driverIdToUse, value) => {
    setComments((prev) => ({ ...prev, [driverIdToUse]: value }));
  };

  const processDriverApproval = async (driverIdToUse, approved) => {
    if (typeof driverIdToUse === 'undefined' || driverIdToUse === null) {
      setError("Cannot process request: Driver ID is missing or invalid.");
      console.error("Attempted to process driver with undefined or null ID:", driverIdToUse);
      return;
    }

    setLoading(true);
    setError(null);
    setMessage(null);
    const remarks = comments[driverIdToUse] || "";

    try {
      const response = await axios.put(
        `${BASE_URL}/${driverIdToUse}/process-approval`, 
        {
          approved: approved,
          remarks: remarks,
          adminId: ADMIN_ID,
        }
      );

      console.log(`Driver ID ${driverIdToUse} processed:`, response.data);
      setMessage(
        `Driver ${
          approved ? "approved" : "rejected"
        } successfully! ID: ${driverIdToUse}`
      );

      setPendingDrivers((prev) =>
        prev.filter((driver) => driver.driverID !== driverIdToUse) 
      );
      setComments((prev) => {
        const newComments = { ...prev };
        delete newComments[driverIdToUse]; 
        return newComments;
      });

    } catch (err) {
      console.error(`Error processing driver ID ${driverIdToUse}:`, err);
      let errorMessage = `Failed to ${approved ? "approve" : "reject"} driver.`;
      if (err.response && err.response.data && err.response.data.message) {
        errorMessage += ` Reason: ${err.response.data.message}`;
      } else if (err.message) {
        errorMessage += ` Error: ${err.message}`;
      }
      setError(errorMessage);
    } finally {
      setLoading(false);
    }
  };

  const handleApproveDriver = (driverIdToUse) => {
    processDriverApproval(driverIdToUse, true);
  };

  const handleRejectDriver = (driverIdToUse) => {
    processDriverApproval(driverIdToUse, false);
  };

  return (
    <Container className="my-4 py-4">
      <Row className="mb-4">
        <Col>
          <h1 className="fs-2 fw-bold text-dark">Admin Dashboard</h1>
          <p className="text-muted">Manage pending driver registrations.</p>
        </Col>
      </Row>

      {loading && (
        <Alert variant="info" className="d-flex align-items-center">
          <Spinner animation="border" size="sm" className="me-2" /> Loading drivers...
        </Alert>
      )}

      {error && <Alert variant="danger">{error}</Alert>}
      {message && <Alert variant="success">{message}</Alert>}

      <Card className="p-4 shadow-sm rounded-3 mb-4">
        <Card.Header className="bg-white border-0 px-0 pt-0 pb-3">
          <h2 className="fs-5 fw-semibold text-dark mb-0">
            Pending Driver Approvals
          </h2>
        </Card.Header>

        {!loading && pendingDrivers.length > 0 ? (
          <Table responsive hover className="align-middle">
            <thead>
              <tr>
                <th className="px-2">Name</th>
                <th className="px-2">License #</th>
                <th className="px-2">Vehicle</th>
                <th className="px-2">Contact</th>
                <th className="px-2">Comment</th>
                <th className="px-2 text-center">Actions</th>
              </tr>
            </thead>
            <tbody>
              {pendingDrivers.map((driver) => (
                <tr key={driver.id}>
                  <td className="px-2">{driver.name}</td>
                  <td className="px-2">{driver.licenseNumber}</td>
                  <td className="px-2">{driver.vehicleModel}</td>
                  <td className="px-2">{driver.contactNumber}</td>
                  <td className="px-2">
                    <Form.Control
                      type="text"
                      placeholder="Add comment"
                      value={comments[driver.driverID] || ""}
                      onChange={(e) =>
                        handleCommentChange(driver.driverID, e.target.value) 
                      }
                      size="sm"
                    />
                  </td>
                  <td className="px-2 text-center">
                    <div className="d-flex justify-content-center gap-2 flex-wrap">
                      <Button
                        variant="success"
                        size="sm"
                        onClick={() => handleApproveDriver(driver.driverID)}
                        disabled={loading}
                      >
                        <CheckCircle size={14} className="me-1" /> Approve
                      </Button>
 
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="text-center py-5">
              <p className="text-muted">No new driver registrations pending.</p>
              <p className="small text-muted">
                All registered drivers have been approved.
              </p>
            </div>
          )
        )}
      </Card>
    </Container>
  );
};

export default Admin;