# ConcordiAPI

**ConcordiAPI** is a Spring Boot backend API for managing users, tasks, and teams. It provides ready-to-use REST endpoints with secure JWT authentication and BCrypt-hashed passwords, along with role-based access control. The API can be deployed using Docker, run as a packaged JAR, or executed locally, offering a flexible and secure foundation for building team and task management solutions.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Releases](#releases)
- [Demo](#demo)

---

## Tech Stack

- **Java 21** - Latest stable version with LTS support.
- **Spring Boot** - Simplifies application setup and dependency management while providing production-ready features.
- **Microsoft SQL Server (T-SQL)** - Reliable relational database system.
- **Docker** - Optional, for containerized deployment.
- **JSON Web Tokens (JWT)** - Token-based authentication.
- **BCrypt** - Industry-standard hashing algorithm for secure password storage.

---

## Features

- **JWT Authentication:** Default token expiration is 60 minutes.
- **Role-based Access Control:** Users and teams with permissions enforced at the API level.
- **RESTful API:** Full CRUD support for users, tasks, and teams.
- **Docker Support:** Build and run the app in a containerized environment.
- **Swagger UI:** Interactive API documentation for exploring and testing endpoints.

---

## Prerequisites

Ensure the following requirements are met before running **ConcordiAPI**:

- **Microsoft SQL Server** (T-SQL compatible) - Local or remote instance.
- **Database** - An existing database with credentials that have read/write privileges.
- **Java 21** - Required if running from source or via a packaged JAR.
- **Docker** - Required only if deploying via container.

> **Important:** The database must be accessible from the machine or container running ConcordiAPI.
> Make sure your `DB_URL` in configuration points to a valid, reachable SQL Server instance.

---

## Installation

### **Option 1 - Docker**
A `Dockerfile` is included in the repository.
You **must** provide the following environment variables for the container to run:

| Variable      | Description |
|---------------|-------------|
| `DB_URL`      | JDBC connection string for SQL Server, e.g., `jdbc:sqlserver://localhost:1433;databaseName=yourdb` |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET`  | *(Optional)* Secret key for JWT token generation. If not provided, a new one is generated on startup. Generating new secret invalidates all previous tokens |

**Build and run with Docker:**
```bash
docker build -t concordiapi .
docker run -e DB_URL=... -e DB_USERNAME=... -e DB_PASSWORD=... -p 10000:10000 concordiapi
```

### **Option 2 - Prebuilt JAR**
The latest release is available in the [releases tab](https://github.com/PatrykMarchewka/ConcordiAPI/releases/).
The `.jar` file is ready to run once the required environment variables are set.

**Run the JAR:**
```bash
java -jar concordiapi.jar \
  --spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=yourdb \
  --spring.datasource.username=yourusername \
  --spring.datasource.password=yourpassword
```
(Add --JWT_SECRET=your_secret if you want a fixed JWT secret.)

### **Option 3 - Local Development**
1. **Clone the repository:**
```bash
git clone https://github.com/PatrykMarchewka/ConcordiAPI.git
cd ConcordiAPI
```
2. Set environment variables and run
   You can set the required environment variables in PowerShell before running the application:
  ```powershell
$env:DB_URL="jdbc:sqlserver://localhost:1433;databaseName=yourdb"
$env:DB_USERNAME="yourusername"
$env:DB_PASSWORD="yourpassword"
./gradlew.bat bootRun
```
Alternatively, you can configure these values directly in the application properties file in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=yourdb
spring.datasource.username=yourusername
spring.datasource.password=yourpassword
jwt.secret=your_jwt_secret
```

---

## Usage

Once the application is running, the API will be accessible at:

**Base URL:** `http://0.0.0.0:10000`

You can interact with the API using any HTTP client, such as [Postman](https://www.postman.com/) or [cURL](https://curl.se/).

### Example: Create a New User

**HTTP Method:** `POST`
**Endpoint:** `/signup`
**Headers:**
- `Content-Type: application/json`

**Request Body:**
```json
{
  "login": "MyLogin",
  "password": "MyPassword",
  "name": "John",
  "lastname": "Doe"
}
```
**Sample Response:**
```json
{
  "message": "User created",
  "data": {
    "id": 1,
    "name": "John",
    "lastName": "Doe"
  },
  "timestamp": "2025-08-13T18:22:45.065092700+02:00"
}
```
**Response Codes:**
- `201 Created` - User successfully created
- `409 Conflict` - User with the specified login already exists
- `500 Internal Server Error` - An unexpected error occurred on the server

---

## API Documentation
- **Visit 0.0.0.0:10000/swagger-ui/index.html for interactive API documentation**
- **Visit 0.0.0.0:10000/v3/api-docs for JSON Open API Docs**

---

## Releases
- **The latest release of the app is available on the [releases tab](https://github.com/PatrykMarchewka/ConcordiAPI/releases/)**

---

## Demo
- **A live demo of this project is hosted on Render. You can access it at the following URL: [Render](https://concordiapi.onrender.com)**
