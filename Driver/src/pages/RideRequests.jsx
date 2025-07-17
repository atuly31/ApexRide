// import React from "react";
// import {
//   MapPin,
//   IndianRupee,
//   Clock,
//   CheckCircle,
//   X,
//   Navigation,
// } from "lucide-react";
// import { useApp } from "../context/AppContext.jsx";

// // Import React-Bootstrap components
// import {
//   Container,
//   Row,
//   Col,
//   Card,
//   Button,
//   Badge,
//   Alert,
// } from "react-bootstrap";

// const RideRequests = () => {
//   const { currentUser, rides, updateRide } = useApp();
//   const pendingRides = rides.filter(
//     (ride) => ride.status === "pending" && !ride.driverId
//   );
//   const acceptedRides = rides.filter(
//     (ride) =>
//       ride.driverId === currentUser?.id &&
//       ride.status !== "completed" &&
//       ride.status !== "cancelled"
//   );

//   const handleAcceptRide = (rideId) => {
//     updateRide(rideId, {
//       driverId: currentUser?.id,
//       status: "accepted",
//       acceptedTime: new Date(),
//     });
//   };

//   const handleStartRide = (rideId) => {
//     updateRide(rideId, { status: "in-progress" });
//   };

//   const handleCompleteRide = (rideId) => {
//     updateRide(rideId, {
//       status: "completed",
//       completedTime: new Date(),
//       actualFare: rides.find((r) => r.id === rideId)?.estimatedFare,
//     });
//   };

//   const getStatusVariant = (status) => {
//     switch (status) {
//       case "accepted":
//         return "warning"; // Often orange/yellow
//       case "in-progress":
//         return "primary"; // Often blue
//       case "pending":
//         return "info"; // Often light blue
//       default:
//         return "secondary";
//     }
//   };

//   return (
//     <Container className="my-4 py-4">
//       <h1 className="fs-2 fw-bold text-dark mb-4">Ride Requests</h1>

//       {/* Current Rides */}
//       {acceptedRides.length > 0 && (
//         <Card className="shadow-sm rounded-3 p-4 mb-4">
//           <Card.Header className="bg-white border-0 px-0 pt-0 pb-3">
//             <h2 className="fs-5 fw-semibold text-dark mb-0">Current Rides</h2>
//           </Card.Header>
//           {/* Changed to d-flex flex-column gap-3 for explicit vertical stacking */}
//           <div className="d-flex flex-column gap-3">
//             {acceptedRides.map((ride) => (
//               // border-2 isn't a direct Bootstrap class; consider custom CSS if exact thickness is critical
//               <Card
//                 key={ride.id}
//                 className="border border-primary bg-primary-subtle p-3"
//               >
//                 <Card.Body className="p-0">
//                   <div className="d-flex justify-content-between align-items-start mb-3">
//                     <div className="flex-grow-1">
//                       <div className="d-flex align-items-center gap-2 mb-3">
//                         <Badge
//                           bg={getStatusVariant(ride.status)}
//                           className="px-2 py-1 fs-6 rounded-pill"
//                         >
//                           {ride.status === "accepted"
//                             ? "Ride Accepted"
//                             : "In Progress"}
//                         </Badge>
//                         <span className="small text-muted">
//                           Accepted at{" "}
//                           {new Date(
//                             ride.acceptedTime || ""
//                           ).toLocaleTimeString()}
//                         </span>
//                       </div>

//                       {/* Changed to d-flex flex-column gap-2 for explicit vertical stacking */}
//                       <div className="d-flex flex-column gap-2 mb-3">
//                         <div className="d-flex align-items-center gap-2">
//                           <MapPin size={16} className="text-success" />
//                           <span className="small fw-medium">
//                             Pickup: {ride.pickup}
//                           </span>
//                         </div>
//                         <div className="d-flex align-items-center gap-2">
//                           <MapPin size={16} className="text-danger" />
//                           <span className="small fw-medium">
//                             Destination: {ride.destination}
//                           </span>
//                         </div>
//                       </div>

//                       <div className="d-flex align-items-center gap-4 small text-muted">
//                         <div className="d-flex align-items-center gap-1">
//                           <Navigation size={16} />
//                           <span>{ride.distance} km</span>
//                         </div>
//                         <div className="d-flex align-items-center gap-1">
//                           <IndianRupee size={16} />
//                           <span>${ride.estimatedFare?.toFixed(2)}</span>
//                         </div>
//                         <div className="d-flex align-items-center gap-1">
//                           <Clock size={16} />
//                           <span>{Math.round(ride.distance * 2)} mins</span>
//                         </div>
//                       </div>
//                     </div>

//                     <div className="d-flex flex-column gap-2">
//                       {" "}
//                       {/* flex flex-col space-y-2 */}
//                       {ride.status === "accepted" && (
//                         <Button
//                           onClick={() => handleStartRide(ride.id)}
//                           variant="primary"
//                           className="d-inline-flex align-items-center px-3 py-2 fw-medium"
//                         >
//                           <Navigation size={16} className="me-2" />
//                           <span>Start Ride</span>
//                         </Button>
//                       )}
//                       {ride.status === "in-progress" && (
//                         <Button
//                           onClick={() => handleCompleteRide(ride.id)}
//                           variant="success"
//                           className="d-inline-flex align-items-center px-3 py-2 fw-medium"
//                         >
//                           <CheckCircle size={16} className="me-2" />
//                           <span>Complete Ride</span>
//                         </Button>
//                       )}
//                     </div>
//                   </div>

//                   {ride.status === "in-progress" && (
//                     <Alert variant="info" className="mt-3 py-2 px-3 small">
//                       <p className="fw-medium text-info-emphasis mb-0">
//                         Ride in Progress
//                       </p>
//                       <p
//                         className="text-info-emphasis mb-0 mt-1"
//                         style={{ fontSize: "0.75rem" }}
//                       >
//                         Navigate to the destination and complete the ride when
//                         you arrive.
//                       </p>
//                     </Alert>
//                   )}
//                 </Card.Body>
//               </Card>
//             ))}
//           </div>
//         </Card>
//       )}

//       {/* Available Ride Requests */}
//       <Card className="shadow-sm rounded-3 p-4 mb-4">
//         <Card.Header className="bg-white border-0 px-0 pt-0 pb-3">
//           <h2 className="fs-5 fw-semibold text-dark mb-0">
//             Available Ride Requests ({pendingRides.length})
//           </h2>
//         </Card.Header>

//         {pendingRides.length > 0 ? (
//           // Changed to d-flex flex-column gap-3 for explicit vertical stacking
//           <div className="d-flex flex-column gap-3">
//             {pendingRides.map((ride) => (
//               <Card
//                 key={ride.id}
//                 className="border border-light rounded-3 p-3"
//                 // Tailwind's hover:bg-gray-50 and transition-colors require custom CSS in Bootstrap.
//                 // For shadow on hover, you'd typically add a custom class and define :hover styles.
//                 // e.g., className="border border-light rounded-3 p-3 custom-hover-card"
//                 // In your CSS: .custom-hover-card:hover { background-color: var(--bs-gray-100); box-shadow: var(--bs-box-shadow-sm); transition: all 0.2s ease-in-out; }
//               >
//                 <Card.Body className="p-0">
//                   <div className="d-flex justify-content-between align-items-start">
//                     <div className="flex-grow-1">
//                       <div className="d-flex align-items-center gap-2 mb-3">
//                         <Badge
//                           bg="warning"
//                           className="px-2 py-1 fs-6 rounded-pill"
//                         >
//                           New Request
//                         </Badge>
//                         <span className="small text-muted">
//                           {new Date(ride.requestTime).toLocaleTimeString()}
//                         </span>
//                       </div>

//                       {/* Changed to d-flex flex-column gap-2 for explicit vertical stacking */}
//                       <div className="d-flex flex-column gap-2 mb-3">
//                         <div className="d-flex align-items-center gap-2">
//                           <MapPin size={16} className="text-success" />
//                           <span className="small fw-medium">
//                             From: {ride.pickup}
//                           </span>
//                         </div>
//                         <div className="d-flex align-items-center gap-2">
//                           <MapPin size={16} className="text-danger" />
//                           <span className="small fw-medium">
//                             To: {ride.destination}
//                           </span>
//                         </div>
//                       </div>

//                       <div className="d-flex align-items-center gap-4 small text-muted">
//                         <div className="d-flex align-items-center gap-1">
//                           <Navigation size={16} />
//                           <span>{ride.distance} km</span>
//                         </div>
//                         <div className="d-flex align-items-center gap-1">
//                           <IndianRupee size={16} />
//                           <span className="fw-medium text-success">
//                             ${ride.estimatedFare?.toFixed(2)}
//                           </span>
//                         </div>
//                         <div className="d-flex align-items-center gap-1">
//                           <Clock size={16} />
//                           <span>~{Math.round(ride.distance * 2)} mins</span>
//                         </div>
//                       </div>
//                     </div>

//                     <div className="d-flex gap-2">
//                       <Button
//                         onClick={() => handleAcceptRide(ride.id)}
//                         variant="success"
//                         className="d-inline-flex align-items-center px-3 py-2 fw-medium"
//                       >
//                         <CheckCircle size={16} className="me-2" />
//                         <span>Accept</span>
//                       </Button>
//                       <Button
//                         variant="secondary"
//                         className="d-inline-flex align-items-center px-3 py-2 fw-medium"
//                       >
//                         <X size={16} className="me-2" />
//                         <span>Decline</span>
//                       </Button>
//                     </div>
//                   </div>
//                 </Card.Body>
//               </Card>
//             ))}
//           </div>
//         ) : (
//           <div className="text-center py-5">
//             <Clock size={64} className="text-muted mx-auto mb-3" />
//             <h3 className="fs-5 fw-medium text-dark mb-2">
//               No requests available
//             </h3>
//             <p className="text-muted mb-4">
//               New ride requests will appear here when riders book rides in your
//               area.
//             </p>
//             <Alert
//               variant="info"
//               className="py-2 px-3 small mx-auto"
//               style={{ maxWidth: "400px" }}
//             >
//               <p className="mb-0 text-info-emphasis">
//                 <strong>Tip:</strong> Make sure your status is set to
//                 "Available" to receive ride requests.
//               </p>
//             </Alert>
//           </div>
//         )}
//       </Card>

//       {/* Instructions */}
//       <Card
//         className="bg-light border border-info rounded-3 p-4"
//         // Tailwind's bg-gradient-to-r from-blue-50 to-indigo-50 would require custom CSS
//         // e.g., style={{ background: 'linear-gradient(to right, #e0e7ff, #eef2ff)' }}
//       >
//         <Card.Body className="p-0">
//           <h3 className="fw-semibold text-info-emphasis mb-3">How it works:</h3>
//           <ul className="list-unstyled small text-info-emphasis mb-0">
//             {/* space-y-1 not directly replicated but default li spacing is usually sufficient */}
//             <li>
//               • New ride requests appear automatically when riders book rides
//             </li>
//             <li>• Accept requests quickly to secure more rides</li>
//             <li>
//               • Use the navigation features to reach pickup and destination
//               points
//             </li>
//             <li>• Complete rides to earn payment and build your rating</li>
//           </ul>
//         </Card.Body>
//       </Card>
//     </Container>
//   );
// };

// export default RideRequests;
