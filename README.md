# 🚖 ApexRide - Ride Booking Platform

ApexRide is a ride-hailing application built with a **Spring Boot microservices architecture** and multiple **frontend apps** for different user roles: Admin, Driver, and User.

---

## 🔧 Tech Stack

-   **Backend**: Java, Spring Boot, Spring Cloud, Spring Security, Eureka, Config Server, API Gateway
-   **Frontend**: React.js (for Admin, Driver, User)
-   **Service Discovery**: Eureka Server (Port `8761`)
-   **API Gateway**: Spring Cloud Gateway
-   **Authentication**: JWT, AuthService
-   **Others**: Config Server, Feign Clients

---

## 🧩 Microservices Architecture

All microservices are registered to **Eureka Server** at port `8761`.

| Microservice | Description | Port |
| --- | --- | --- |
| `EurekaServer` | Service registry for all microservices | `8761` |
| `Config-Server` | Centralized config server | `8090` |
| `Gateway` | Routes requests to the appropriate services | `8086` |
| `AuthService` | Handles user authentication & JWT tokens | `8070` |
| `User` | Manages user details and bookings | `8080` |
| `Driver` | Manages driver data and availability | `8081` |
| `Admin` | Admin operations (managing rides, drivers) | `8087` |
| `Payment` | Handles payments for rides | `8084` |
| `Ride` | Manages ride creation, history, status | `8082` |


---

## 💻 Frontend Apps

| Frontend | Path | Description |
| --- | --- | --- |
| `User Frontend` | `/User-Frontend` | Allows user to book rides |
| `Driver Frontend`| `/Driver-Frontend` | Drivers manage ride status |
| `Admin Frontend` | `/Admin-Frontend` | Admin dashboard for operations |

> Each frontend app should interact with the API Gateway at `http://localhost:8086`.

---

## ✅ Step-by-Step Setup

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/atuly31/ApexRide.git
cd ApexRide
```
## 2️⃣ 📦 Backend Setup

Make sure you have **Java 17+** and **Maven** installed on your machine.

---

### ✅ Run Order (Very Important)

Start backend services in the following order to avoid configuration and registration issues:

1. **EurekaServer**  
   - Port: `8761`  
   - Acts as the service registry for all other microservices.

2. **Config-Server**  
   - Port: `8090`  
   - Provides centralized configuration to all services.  
   - GitHub Config Repo: [apexride-config](https://github.com/atuly31/ApexRide-Config-server)

3. **Gateway**  
   - Port: `8086`  
   - Serves as the API gateway to route external and internal requests.

---

### ▶️ Start Remaining Microservices (In Any Order)

| Service Name   | Description                           | Port    |
|----------------|---------------------------------------|---------|
| `AuthService`  | Handles login, JWT auth               | `8070`  |
| `User`         | User registration, bookings, details  | `8080`  |
| `Driver`       | Driver onboarding, availability       | `8081`  |
| `Admin`        | Admin dashboard and management        | `8087`  |
| `Payment`      | Payment and transaction handling      | `8084`  |
| `Ride`         | Ride history, ride tracking           | `8082`  |


---

### ▶️ Run Any Microservice

To run a microservice using Maven:

```bash
cd "Backend Code/ServiceName"
mvn spring-boot:run
```
## 3️⃣ 🌐 Frontend Setup

Make sure you have **Node.js** and **npm** installed on your system.

---

### ▶️ How to Run a Frontend App

Follow the steps below to run any frontend app:

```bash
cd FrontendFolder
npm install
npm start
```
🔁 Replace FrontendFolder with the actual folder name, such as:

User-Frontend

Driver-Frontend

Admin-Frontend

---
## 4️⃣ 🔄 Configuration After Cloning

After cloning the project, make sure to follow these configuration steps:

---

### ✅ Essential Setup Steps

- Ensure all `application.properties` or `application.yml` files in each microservice **correctly point to the Config Server**.
- The **Config-Server must be started before any other microservice**.
- All microservices must successfully register with **Eureka** at: http://localhost:8761/eureka
  

- Make sure your local **database names and credentials** match those defined in the [apexride-config](https://github.com/your-org/apexride-config) repository.

---

### 🔑 Third-Party Services (Optional)

If your app uses third-party services like:

- Email providers (SMTP)
- Stripe (Payments)
- SMS gateways
- Any external API

👉 Update those credentials in the **apexride-config** repository under the appropriate environment files (`application-dev.yml`, etc.).

---

## 📄 License

This project is open-source under the **MIT License**.

---

## 🙋‍♂️ Author

**Atul Yadav**  
GitHub: [@atuly31](https://github.com/atuly31)


