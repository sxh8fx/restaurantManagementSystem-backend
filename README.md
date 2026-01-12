# The Overlook Restaurant - Backend API

This is the backend server for **The Overlook Restaurant**, a smart dining management system. It handles user authentication, reservations, menu management, tables, and real-time order processing.

## 🛠️ Technology Stack
- **Framework:** Spring Boot 3 (Java 17)
- **Database:** MySQL 8 (Aiven / Local)
- **Security:** Spring Security + JWT (JSON Web Tokens)
- **Real-time:** Spring WebSocket (STOMP)
- **Build Tool:** Maven

## 🔗 Related Repositories
- **Frontend:** [restaurantManagementSystem-frontend](https://github.com/sxh8fx/restaurantManagementSystem-frontend)

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven
- MySQL Database

### 1. Environment Setup
Create a `.env` file in the `backend` root directory (this file is git-ignored):

```env
DB_URL=jdbc:mysql://YOUR_DB_HOST:3306/restaurant_db?createDatabaseIfNotExist=true&useSSL=true
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```

### 2. Run Locally
```bash
mvn spring-boot:run
```
The server will start at `http://localhost:8080`.

## 🐳 Docker Deployment

The application is containerized using Docker.

### Build & Run
```bash
# Build the image
docker build -t restaurant-backend .

# Run the container (passing env vars)
docker run -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host:3306/db" \
  -e DB_USERNAME="root" \
  -e DB_PASSWORD="password" \
  restaurant-backend
```

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/signin` - Login & get JWT
- `POST /api/auth/signup` - Register new user

### Menu (Public)
- `GET /api/menu` - Get all menu items
- `GET /api/menu/{category}` - Get items by category

### Reservations
- `POST /api/reservations` - Create a reservation
- `GET /api/reservations/user` - Get user history

### Admin (Protected)
- `GET /api/admin/orders` - View all orders
- `POST /api/menu` - Add menu item
- `PUT /api/menu/{id}` - Update menu item

## 💓 Health Checks
- `GET /` - "Restaurant Backend is running!"
- `GET /health` - "OK" (Used by deployment platforms)
