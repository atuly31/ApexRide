import React, { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, useMap } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import "leaflet-routing-machine/dist/leaflet-routing-machine.css";
import "leaflet-routing-machine";
import markerIconPng from "leaflet/dist/images/marker-icon.png";
import markerShadowPng from "leaflet/dist/images/marker-shadow.png";

const defaultIcon = L.icon({
  iconUrl: markerIconPng,
  shadowUrl: markerShadowPng,
  iconAnchor: [12, 41],
});
 
const Routing = ({ pickup, drop, setRouteInfo }) => {
  const map = useMap();
 
  useEffect(() => {
    if (!pickup || !drop) return;
 
    const routingControl = L.Routing.control({
      waypoints: [
        L.latLng(pickup.lat, pickup.lng),
        L.latLng(drop.lat, drop.lng),
      ],
      routeWhileDragging: false,
      show: false,
      addWaypoints: false,
      draggableWaypoints: false,
      fitSelectedRoutes: true,
      createMarker: () => null,
    }).addTo(map);
 
    routingControl.on("routesfound", function (e) {
      const route = e.routes[0];
      const summary = route.summary;
      const distanceKm = (summary.totalDistance / 1000).toFixed(2);
      const timeMin = Math.ceil(summary.totalTime / 60);
      setRouteInfo({ distance: distanceKm, time: timeMin });
    });
 
    const container = document.querySelector(".leaflet-routing-container");
    if (container) container.style.display = "none";
 
    return () => map.removeControl(routingControl);
  }, [pickup, drop, map, setRouteInfo]);
 
  return null;
};
 
const MapPreview = ({ pickup, drop, setRouteInfo }) => {
  const [center, setCenter] = useState([13.0827, 80.2707]); 
  const [locationReady, setLocationReady] = useState(false);
 
  useEffect(() => {
    if (!pickup && !drop) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setCenter([position.coords.latitude, position.coords.longitude]);
          setLocationReady(true);
        },
        () => {
          console.warn("Geolocation not available or permission denied.");
          setCenter([13.0827, 80.2707]); 
          setLocationReady(true);
        }
      );
    } else if (pickup) {
      setCenter([pickup.lat, pickup.lng]);
      setLocationReady(true);
    }
  }, [pickup, drop]);
 
  return (
    <div style={{ position: "relative" }}>
      <MapContainer
        center={center}
        zoom={13}
        style={{ height: "400px", width: "100%" }}
        scrollWheelZoom={true}
      >
        <TileLayer
          attribution='&copy; https://www.openstreetmap.org/ contributors'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />
        {locationReady && !pickup && !drop && (
          <Marker position={center} icon={defaultIcon} />
        )}
        {pickup && drop && (
          <Routing pickup={pickup} drop={drop} setRouteInfo={setRouteInfo} />
        )}
      </MapContainer>
    </div>
  );
};
 
export default MapPreview;
 
 