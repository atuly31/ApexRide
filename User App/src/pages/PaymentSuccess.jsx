import React from "react";
import { useNavigate } from "react-router-dom";
import { Button, Container, Card } from "react-bootstrap"; 

const PaymentSuccess = () => {
  const navigate = useNavigate();

  return (
    <Container className="py-4" style={{ maxWidth: "700px" }}> 
      <Card className="shadow-sm rounded-3 p-4 mb-4 text-center"> 
        <Card.Body>
          <h2 className="mb-4 fs-3 fw-semibold text-success">🎉 Payment Successful!</h2> 
          <p className="mb-4 text-dark">Thank you for riding with us. Your payment has been processed successfully, and you're all set.</p>
          
          <Button 
            onClick={() => navigate("/dashboard")} 
            className="w-100 shadow py-3 text-dark fw-bold" 
            style={{ background: "rgba(241, 211, 2, 1)", border: "none" }} 
          >
            Go to Dashboard
          </Button>
        </Card.Body>
      </Card>
    </Container>
  );
};

export default PaymentSuccess;