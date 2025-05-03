# ConcordiAPI

A Spring Boot application for managing tasks, users, and teams, offering a REST API with full CRUD functionality (GET, POST, PUT, DELETE). Key features include:
- Tasks & Subtasks: Create, update, delete tasks and subtasks, with assignments and status tracking.
- Teams: Create and manage teams, assign users, and send invitations.
- User Roles: Different roles (e.g., Admin, Member) with specific permissions.
- Invitations: Users can invite others to teams and manage invitations.
- API Documentation: Access to Swagger UI for easy endpoint reference.
- Dockerized: The app is containerized with Docker for easy deployment.

---

## Table of Contents

- [Features](#features)
- [Requirements](#requirements)
- [Installation](#installation)
- [Usage](#usage)
- [API Documentation](#api-documentation)
- [Releases](#releases)
- [Demo](#demo)

---

## Features

- **Authentication**: Secure login using BCrypt and JSON Web Tokens with user roles management and team-based permissions.
- **Task Management**: Create, update, delete tasks and subtasks, assign them to users, and track their progress.
- **Teams & Invitations**: Manage teams, invite users, and handle pending invitations.
- **User Roles**: Assign and manage different user roles (Admin, Member, etc.) for access control.
- **Swagger UI**: Interactive API documentation for easier integration and testing.
- **Docker Support**: Fully dockerized for containerization and deployment in any environment.
  
---

## Requirements

- **Java 21** or later (JDK)
- **Gradle** for build automation
- **Docker** (optional for containerization)
- **API testing tool** to send requests to endpoints

---

## Installation

- **You can download the jar file from [releases tab](https://github.com/PatrykMarchewka/ConcordiAPI/releases/) or clone the repository** 
- **Make sure to edit application.properties with your database connection string and optionally set your JWT Secret**

## Usage

- **After running the app, you can access the API at 0.0.0.0:10000**
- **Use any API testing tool to test the exposed endpoints**
- **Example Request (POST)**
To create a new user, send a post request to /signup

```json
{
"login":"MyLogin",
"password":"MyPassword",
"name":"John",
"lastname":"Doe"
}
```

## API Documentation
- **Visit 0.0.0.0:10000/swagger-ui/index.html for interactive API documentation**
- **Visit 0.0.0.0:10000/v3/api-docs for JSON Open API Docs**

## Releases
- **The latest release of the app is available on the [releases tab](https://github.com/PatrykMarchewka/ConcordiAPI/releases/)**

## Demo
- **A live demo of this project is hosted on Render. You can access it at the following URL: [Render](https://concordiapi.onrender.com)**
