// import React, { useState } from "react";
// import { MapPin, Star, Clock, DollarSign, MessageSquare } from "lucide-react";
// // import { useApp } from "../context/AppContext.jsx";

// // Import React-Bootstrap components
// import {
//   Container,
//   Row,
//   Col,
//   Card,
//   Button,
//   Badge,
//   Form,
//   // InputGroup is not used in this specific file but generally useful for forms
// } from "react-bootstrap";

// const RideHistory = () => {
//   const { currentUser, rides, drivers, updateRide } = useApp();
//   const [selectedRide, setSelectedRide] = useState(null);
//   const [rating, setRating] = useState(0);
//   const [review, setReview] = useState("");

//   const userRides = rides
//     .filter((ride) => ride.userId === currentUser?.id)
//     .sort(
//       (a, b) =>
//         new Date(b.requestTime).getTime() - new Date(a.requestTime).getTime()
//     );

//   const handleRating = (rideId, ratingValue) => {
//     setSelectedRide(rideId);
//     setRating(ratingValue);
//   };

//   const submitRating = (rideId) => {
//     updateRide(rideId, { rating, review });
//     setSelectedRide(null);
//     setRating(0);
//     setReview("");
//   };

//   const getDriverName = (driverId) => {
//     if (!driverId) return "Driver not assigned";
//     const driver = drivers.find((d) => d.id === driverId);
//     return driver ? driver.name : "Unknown Driver";
//   };

//   const getStatusVariant = (status) => {
//     switch (status) {
//       case "completed":
//         return "success";
//       case "in-progress":
//         return "primary";
//       case "accepted":
//         return "warning"; // Using warning for accepted for distinction
//       case "pending":
//         return "info"; // Using info for pending
//       case "cancelled":
//         return "danger";
//       default:
//         return "secondary";
//     }
//   };

//   return (
//     <Container className="my-4 py-4">
//       <Row className="mb-4 d-flex justify-content-between align-items-center">
//         <Col>
//           <h1 className="fs-2 fw-bold text-dark">Ride History</h1>
//         </Col>
//         <Col xs="auto">
//           <div className="small text-muted">
//             Total Rides: {userRides.length}
//           </div>
//         </Col>
//       </Row>

//       {userRides.length > 0 ? (
//         // Changed to d-flex flex-column gap-4 for explicit vertical stacking
//         <div className="d-flex flex-column gap-4">
//           {userRides.map((ride) => (
//             <Card key={ride.id} className="shadow-sm rounded-3 p-4">
//               <Card.Body className="p-0">
//                 <div className="d-flex justify-content-between align-items-start mb-3">
//                   <div className="flex-grow-1">
//                     <div className="d-flex align-items-center gap-2 mb-2">
//                       {/* Using rounded-pill for more pronounced badge shape */}
//                       <Badge
//                         bg={getStatusVariant(ride.status)}
//                         className="px-2 py-1 fs-6 rounded-pill"
//                       >
//                         {ride.status.charAt(0).toUpperCase() +
//                           ride.status.slice(1)}
//                       </Badge>
//                       <span className="small text-muted">
//                         {new Date(ride.requestTime).toLocaleDateString()} at{" "}
//                         {new Date(ride.requestTime).toLocaleTimeString()}
//                       </span>
//                     </div>

//                     {/* Changed to d-flex flex-column gap-2 for explicit vertical stacking */}
//                     <div className="d-flex flex-column gap-2 mb-3">
//                       <div className="d-flex align-items-center gap-2">
//                         <MapPin size={16} className="text-success" />
//                         <span className="small text-dark">
//                           From: {ride.pickup}
//                         </span>
//                       </div>
//                       <div className="d-flex align-items-center gap-2">
//                         <MapPin size={16} className="text-danger" />
//                         <span className="small text-dark">
//                           To: {ride.destination}
//                         </span>
//                       </div>
//                     </div>

//                     <div className="d-flex align-items-center gap-4 small text-muted">
//                       <div className="d-flex align-items-center gap-1">
//                         <Clock size={16} />
//                         <span>{ride.distance} km</span>
//                       </div>
//                       <div className="d-flex align-items-center gap-1">
//                         <DollarSign size={16} />
//                         <span>
//                           ${(ride.actualFare || ride.estimatedFare)?.toFixed(2)}
//                         </span>
//                       </div>
//                       <span>Driver: {getDriverName(ride.driverId)}</span>
//                     </div>
//                   </div>

//                   <div className="text-end">
//                     {ride.rating ? (
//                       <div className="d-flex align-items-center gap-1 mb-2">
//                         <Star
//                           size={16}
//                           className="text-warning"
//                           fill="currentColor"
//                         />
//                         <span className="small fw-medium">
//                           {ride.rating.toFixed(1)}
//                         </span>
//                       </div>
//                     ) : (
//                       ride.status === "completed" && (
//                         <Button
//                           variant="link"
//                           onClick={() => handleRating(ride.id, 0)}
//                           className="small fw-medium text-decoration-none p-0"
//                         >
//                           Rate Ride
//                         </Button>
//                       )
//                     )}
//                   </div>
//                 </div>

//                 {/* Rating Section (Inline) */}
//                 {selectedRide === ride.id && (
//                   <div className="border-top pt-4 mt-4">
//                     <h4 className="fw-medium text-dark mb-3">Rate Your Ride</h4>
//                     <div className="d-flex align-items-center gap-2 mb-3">
//                       {[1, 2, 3, 4, 5].map((star) => (
//                         <Button
//                           key={star}
//                           variant="link"
//                           onClick={() => setRating(star)}
//                           className={`fs-4 p-0 text-decoration-none ${
//                             star <= rating ? "text-warning" : "text-secondary"
//                           }`}
//                           style={{ transition: "color 0.2s" }}
//                         >
//                           ★
//                         </Button>
//                       ))}
//                       <span className="small text-muted ms-2">
//                         {rating === 0
//                           ? "Select rating"
//                           : rating === 1
//                           ? "Poor"
//                           : rating === 2
//                           ? "Fair"
//                           : rating === 3
//                           ? "Good"
//                           : rating === 4
//                           ? "Very Good"
//                           : "Excellent"}
//                       </span>
//                     </div>

//                     <Form.Group className="mb-3">
//                       <Form.Label className="small fw-medium text-muted mb-1">
//                         Review (Optional)
//                       </Form.Label>
//                       <Form.Control
//                         as="textarea"
//                         value={review}
//                         onChange={(e) => setReview(e.target.value)}
//                         rows={3}
//                         placeholder="Share your experience..."
//                         style={{ resize: "none" }} // Replicates Tailwind's resize-none
//                       />
//                     </Form.Group>

//                     <div className="d-flex gap-3">
//                       <Button
//                         onClick={() => submitRating(ride.id)}
//                         disabled={rating === 0}
//                         variant="primary"
//                         className="fw-medium px-4 py-2"
//                       >
//                         Submit Rating
//                       </Button>
//                       <Button
//                         onClick={() => setSelectedRide(null)}
//                         variant="secondary"
//                         className="fw-medium px-4 py-2"
//                       >
//                         Cancel
//                       </Button>
//                     </div>
//                   </div>
//                 )}

//                 {/* Display existing review */}
//                 {ride.review && (
//                   <div className="border-top pt-4 mt-4">
//                     <div className="d-flex align-items-center gap-2 mb-2">
//                       <MessageSquare size={16} className="text-muted" />
//                       <span className="small fw-medium text-dark">
//                         Your Review
//                       </span>
//                     </div>
//                     <p className="small text-muted fst-italic">
//                       "{ride.review}"
//                     </p>
//                   </div>
//                 )}
//               </Card.Body>
//             </Card>
//           ))}
//         </div>
//       ) : (
//         <Card className="shadow-lg p-5 text-center">
//           <Clock size={48} className="text-muted mx-auto mb-3" />
//           <h3 className="fs-5 fw-medium text-dark mb-2">No rides yet</h3>
//           <p className="text-muted mb-4">
//             Your ride history will appear here once you book your first ride.
//           </p>
//           <Button
//             onClick={() => (window.location.href = "/book-ride")}
//             variant="primary"
//             className="px-4 py-2 fw-medium"
//           >
//             Book Your First Ride
//           </Button>
//         </Card>
//       )}
//     </Container>
//   );
// };

// export default RideHistory;
