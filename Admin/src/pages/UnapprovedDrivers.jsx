import React from "react";
import { Container, Row, Col, Card, Table } from "react-bootstrap";

const UnapprovedDrivers = () => {
 
  const unapprovedDrivers = [];

  return (
    <Container className="my-4 py-4">
      <Row className="mb-4">
        <Col>
          <h1 className="fs-2 fw-bold text-dark">Unapproved Drivers</h1>
          <p className="text-muted">Drivers who were rejected during registration.</p>
        </Col>
      </Row>

      <Card className="p-4 shadow-sm rounded-3 mb-4">
        <Card.Header className="bg-white border-0 px-0 pt-0 pb-3">
          <h2 className="fs-5 fw-semibold text-dark mb-0">Rejected Applications</h2>
        </Card.Header>

        <Table responsive hover className="align-middle">
          <thead>
            <tr>
              <th className="px-2">Name</th>
              <th className="px-2">License #</th>
              <th className="px-2">Vehicle</th>
              <th className="px-2">Contact</th>
              <th className="px-2">Registration Date</th>
              <th className="px-2">Remarks</th>
            </tr>
          </thead>
          <tbody>
            {unapprovedDrivers.map((driver) => (
              <tr key={driver.id}>
                <td className="px-2">{driver.name}</td>
                <td className="px-2">{driver.licenseNumber}</td>
                <td className="px-2">{driver.vehicleModel}</td>
                <td className="px-2">{driver.contactNumber}</td>
                <td className="px-2">
                  {new Date(driver.registrationDate).toLocaleDateString()}
                </td>
                <td className="px-2 text-danger">{driver.remarks}</td>
              </tr>
            ))}
          </tbody>
        </Table>
      </Card>
    </Container>
  );
};

export default UnapprovedDrivers;
