# ConcordiAPI

**ConcordiAPI** is a Spring Boot backend API for managing users, tasks, and teams. It provides ready-to-use REST endpoints with secure JWT authentication and BCrypt-hashed passwords, along with role-based access control. The API can be deployed using Docker, run as a packaged JAR, or executed locally, offering a flexible and secure foundation for building team and task management solutions.

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [System Properties](#system-properties)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Releases](#releases)
- [Demo](#demo)

---

## Tech Stack

- **Java 21** - Latest stable version with LTS support.
- **Spring Boot** - Simplifies application setup and dependency management while providing production-ready features.
- **Relational Database (JDBC-compatible)** - Any database with a supported JDBC driver. *(Microsoft SQL Server with T-SQL used in development)*
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

- **Relational Database** - Any JDBC-compatible database with a valid driver. You must provide credentials with read/write privileges.
- **Java 21** - Required if running from source or via a packaged JAR.
- **Docker** - Required only if deploying via container.

> **Supported Databases:** PostgreSQL, MySQL, MariaDB and Microsoft SQL Server, Apache Derby, SQLite, H2 are supported for production use.
> 
> **Important:** The database must be accessible from the machine or container running ConcordiAPI.

---

## System Properties

ConcordiAPI supports loading system properties from a local file named `ConcordiAPI.env` located in the same directory as the compiled JAR.

**Format:**  
Each variable must be defined on a separate line using the format:
<pre>
Key=Value
AnotherKey=AnotherValue
</pre>
**Example:**
<pre>
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=yourdb
DB_USERNAME=yourusername
DB_PASSWORD=yourpassword
DB_DRIVER=com.microsoft.sqlserver.jdbc.SQLServerDriver
JWT_SECRET=my_custom_secret
SERVER_PORT=10000
</pre>

> **Note:** All values in `ConcordiAPI.env` are loaded as system properties before application startup. These properties are isolated to the ConcordiAPI process and are removed once the application shuts down. Other processes do not have access to these values.

### Supported Variables

| Variable                        | Default Value      | Required | Accepted Values                                       | Description                                                                                                                                                                                                                         |
|---------------------------------|--------------------|----------|-------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `DB_URL`                        | *(none)*           | ✅ Yes    | Valid JDBC URL                                        | Connection string for any supported database                                                                                                                                                                                        |
| `DB_USERNAME`                   | *(none)*           | ✅ Yes    | Any string                                            | Database username                                                                                                                                                                                                                   |
| `DB_PASSWORD`                   | *(none)*           | ✅ Yes    | Any string                                            | Database password                                                                                                                                                                                                                   |
| `DB_DRIVER`                     | *(none)*           | ✅ Yes    | Fully qualified class name                            | JDBC driver class name                                                                                                                                                                                                              |
| `DDL_AUTO`                      | `validate`         | ❌ No     | `none`, `validate`, `update`, `create`, `create-drop` | JPA schema generation strategy                                                                                                                                                                                                      |
| `SHOW_SQL`                      | `false`            | ❌ No     | `true`, `false`                                       | Enables SQL logging for JPA operations                                                                                                                                                                                              |
| `FORMAT_SQL`                    | `false`            | ❌ No     | `true`, `false`                                       | Formats SQL output for readability                                                                                                                                                                                                  |
| `DB_POOL_SIZE`                  | `10`               | ❌ No     | Any positive integer                                  | Maximum size of Hikari connection pool                                                                                                                                                                                              |
| `DB_TIMEOUT`                    | `30000`            | ❌ No     | Milliseconds (integer)                                | Hikari connection timeout                                                                                                                                                                                                           |
| `CORS_ALLOWED_ORIGINS`          | *(none)*           | ❌ No     | Comma-separated URLs                                  | Allowed origins for CORS                                                                                                                                                                                                            |
| `GRAPH_QL_CORS_ALLOWED_ORIGINS` | *(none)*           | ❌ No     | Comma-separated URLs                                  | Allowed origins for GraphQL CORS                                                                                                                                                                                                    |
| `SERVER_ADDRESS`                | `0.0.0.0`          | ❌ No     | Any valid IP or hostname                              | Address the server binds to                                                                                                                                                                                                         |
| `SERVER_PORT`                   | `10000`            | ❌ No     | Any open port                                         | Port on which the API runs                                                                                                                                                                                                          |
| `HEADERS_STRATEGY`              | `framework`        | ❌ No     | `native`, `framework`, `none`                         | Configures how the application handles ` X-Forwarded-*` headers from reverse proxies. Choose between using the web server’s native handling `(NATIVE)`, Spring’s internal support `(FRAMEWORK)`, or ignoring these headers `(NONE)` |
| `TOMCAT_MAX_THREADS`            | `200`              | ❌ No     | Any positive integer                                  | Maximum amount of Tomcat worker threads                                                                                                                                                                                             |
| `COMPRESSION_ENABLED`           | `false`            | ❌ No     | `true`, `false`                                       | Enables compression of server responses                                                                                                                                                                                             |
| `COMPRESSION_TYPES`             | `application/json` | ❌ No     | Comma-seperated MIME types                            | List of MIME types that should be compressed (requires server compression to be enabled)                                                                                                                                            |
| `JWT_SECRET`                    | *(none)*           | ❌ No     | Any string                                            | Secret key for JWT token signing                                                                                                                                                                                                    |
| `JWT_EXPIRATION`                | `3600`             | ❌ No     | Seconds (integer)                                     | Duration before JWT token expires                                                                                                                                                                                                   |
| `LOG_LEVEL`                     | `info`             | ❌ No     | `trace`, `debug`, `info`, `warn`, `error`             | Root logging level                                                                                                                                                                                                                  |
| `API_DOCS`                      | `true`             | ❌ No     | `true`, `false`                                       | Enables `/v3/api-docs` generation                                                                                                                                                                                                   |
| `SWAGGER_UI`                    | `true`             | ❌ No     | `true`, `false`                                       | Enables Swagger UI (requires api-docs generation to be enabled)                                                                                                                                                                     |
| `SWAGGER_PATH`                  | *(none)*           | ❌ No     | Any string                                            | Custom path for Swagger UI                                                                                                                                                                                                          |

> **Note:** If `JWT_SECRET` is omitted, a new secret is generated on each run, invalidating previously issued tokens.

---

## Installation

You can run ConcordiAPI using Docker, a prebuilt JAR, or directly from source.

Environment variables can be provided in two ways:

- **Inline** using command-line flags (`--spring.datasource.url=...` or `-e DB_URL=...`)
- **File-based** using a `ConcordiAPI.env` file placed next to the JAR (see [System Properties](#system-properties))

---

### **Option 1 - Docker**
A `Dockerfile` is included in the repository.

You can pass environment variables directly using `-e`, or rely on a `ConcordiAPI.env` file.

**Build and run with Docker:**
```bash
docker build -t concordiapi .
docker run -e DB_URL=... -e DB_USERNAME=... -e DB_PASSWORD=... -e DB_DRIVER=... -p 10000:10000 concordiapi
```

Or simply:
```bash
docker run -p 10000:10000 concordiapi
```

> If `ConcordiAPI.env` is present in the container’s working directory, its contents will be loaded automatically.

---

### **Option 2 - Prebuilt JAR**
The latest release is available in the [releases tab](https://github.com/PatrykMarchewka/ConcordiAPI/releases/).

You can pass system properties inline or use a `ConcordiAPI.env` file in the same directory as the JAR.

**Run the JAR with inline properties:**
```bash
java -jar concordiapi.jar \
  --spring.datasource.url=jdbc:postgresql://localhost:5432/mydb \
  --spring.datasource.username=myuser \
  --spring.datasource.password=mypassword \
  --spring.datasource.driver-class-name=org.postgresql.Driver
```

Or simply:
```bash
java -jar concordiapi.jar
```

> If `ConcordiAPI.env` is present in the JAR directory, its contents will be loaded automatically.

---

### **Option 3 - Local Development**
1. **Clone the repository:**
```bash
git clone https://github.com/PatrykMarchewka/ConcordiAPI.git
cd ConcordiAPI
```

2. Set system properties and run:
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/mydb"
$env:DB_USERNAME="myuser"
$env:DB_PASSWORD="mypassword"
$env:DB_DRIVER="org.postgresql.Driver"
./gradlew.bat bootRun
```

Alternatively, configure values directly in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=myuser
spring.datasource.password=mypassword
spring.datasource.driver-class-name=org.postgresql.Driver
jwt.secret=your_jwt_secret
```

> You can also create a `ConcordiAPI.env` file in the root directory and run the app without setting variables manually.

---

## Usage

Once the application is running, the API will be accessible at:

**Base URL:** `http://0.0.0.0:10000` unless changed by system properties

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
- **Visit /swagger-ui/index.html for interactive API documentation**
- **Visit /v3/api-docs for JSON Open API Docs**

---

## Releases
- **The latest release of the app is available on the [releases tab](https://github.com/PatrykMarchewka/ConcordiAPI/releases/)**

---

## Demo
- **A live demo of this project is hosted on Render. You can access it at the following URL: [Render](https://concordiapi.onrender.com)**
