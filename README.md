# ConcordiAPI

Backend web application for creating tasks and assigning them to users. The application serves as a REST API supporting GET and POST requests and includes a local admin console for managing the system directly from the host machine. Built with **Spring Boot**, supports modern features like API documentation via **Swagger UI**. The project is containerized with **Docker** for easy deployment.

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

- **Authentication**: Secure login, user roles management.
- **Task Management**: Create, update, delete tasks, and more.
- **Swagger UI**: API documentation for easier integration.
- **Docker Support**: Fully dockerized for containerization and deployment.
  
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
- **Visit http://localhost:8080/v3/api-docs for JSON Open API Docs**

## Releases
- **The latest release of the app is available on the [releases tab](https://github.com/PatrykMarchewka/ConcordiAPI/releases/)**

## Demo
- **A live demo of this project is hosted on Render. You can access it at the following URL: [Render](https://concordiapi.onrender.com)**
