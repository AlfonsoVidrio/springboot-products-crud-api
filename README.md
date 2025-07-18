# Spring Boot Products CRUD API

REST API built with Spring Boot to manage products and users, with access control using JWT tokens and Spring Security.

## Table of Contents
1. [Technologies Used](#technologies-used)
2. [Features](#features)
3. [Requirements](#requirements)
4. [Environment Variables Setup](#environment-variables-setup)
    - [Required Variables](#required-variables)
    - [Configuration Example](#configuration-example)
5. [Database Setup](#database-setup)
    - [Create the Database](#create-the-database)
    - [Tables and Initial Setup](#tables-and-initial-setup)
    - [Important](#important)
    - [Example: Insert admin manually](#example-insert-admin-manually)
6. [Accessing Protected Routes](#accessing-protected-routes)
7. [Documentation](#documentation)


## Technologies Used

- **Java 17:** Programming language.  
- **Spring Boot 3.2.11:** Framework for building the REST API.  
- **Spring Data JPA:** Data persistence and ORM.  
- **Spring Boot Security:** Security management and JWT authentication.  
- **Spring Validation:** Input and data validation.  
- **Springdoc OpenAPI:** API documentation generation with Swagger UI.  
- **Flyway:** Database versioning and migrations.  
- **MySQL Connector/J:** MySQL database driver.  
- **JJWT (io.jsonwebtoken):** JWT token creation and validation.  


## Features

- Register new users with the default `ROLE_USER` role.  
- Create additional admin users (requires both `ROLE_USER` and `ROLE_ADMIN` roles).  
- Retrieve a list of all registered users.  
- Create, update, retrieve (single or all), and delete products.  
- Secure endpoints with JWT-based authentication.  
- Role-based access control to protect sensitive operations.  


## Requirements

- **Java 17** or higher.
- **MySQL** as the database system.
- **Maven** as the dependency manager.

## Environment Variables Setup

For the application to work correctly, you need to configure the following environment variables. These are used to connect to the MySQL database.

### Required Variables

- **`DB_HOST`**: The address or hostname of the MySQL database (e.g., `localhost`).  
- **`DB_PORT`**: The port number where the MySQL server is listening (default: `3306`).  
- **`DB_NAME`**: The name of the MySQL database to connect to.  
- **`DB_USERNAME`**: The username used to access the database.  
- **`DB_PASSWORD`**: The password for the given database user.  

### Configuration Example

```properties
spring.application.name=springboot-crud

spring.datasource.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true
```
Ensure that these variables are properly configured before running the application.

## Database Setup

Before running the application, you need to create the MySQL database that the app will connect to. The database name should match the value you set in the `${DB_NAME}` environment variable.

### Create the Database

Run the following SQL command in your MySQL server:

```sql
CREATE DATABASE your_database_name;
```
Replace `your_database_name` with the actual name (the value of ${DB_NAME}).

### Tables and Initial Setup

Once the application runs for the first time, the following will happen automatically:

- All required tables will be created via **Flyway migrations**.
- Default roles (`ROLE_ADMIN` and `ROLE_USER`) will be inserted automatically into the `role` table.  
  By default:
  - `ROLE_ADMIN` has ID `1`
  - `ROLE_USER` has ID `2`
- Every new user automatically receives the `ROLE_USER` role.

### Important

After the tables are created, you can register users normally using the `/api/users/register` endpoint.

However, no user will have admin privileges initially.  
To grant admin access to a user, you must manually assign the `ROLE_ADMIN` role in the `users_roles` table.

Admins are users who have **both** roles (`ROLE_USER` assigned automatically, and `ROLE_ADMIN` assigned manually).

Only users with both roles can create other admin users via the API.

### Example: Insert admin manually

Example: Assign admin privileges to the first registered user (assuming `user_id = 1`):

```sql
INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);
```

> Once the first admin is set up, that user can manage roles for others through the secured endpoints.

From this point on, users with both `ROLE_USER` and `ROLE_ADMIN` roles can create additional admin users via the API.  

Specifically, they can use the protected endpoint:

- `POST /api/users`  

to create new users and assign them the admin role, without manual database intervention.

This ensures that only authorized admins can promote other users to admin status, maintaining security and proper role management.

---

### Accessing Protected Routes

To access protected endpoints, you must first authenticate via the default login endpoint:

- `POST /login`

Request body example:

```json
{
  "username": "your_username",
  "password": "your_password"
}
```

If authentication is successful, the server will respond with a JWT token which must be included in the Authorization header as:

Authorization: Bearer <token>

When using Postman, add the token in the Authorization tab by selecting Auth Type as Bearer Token, then paste the JWT token in the token field.

This token grants access to the secured API routes according to the user’s roles.

## Documentation
You can view the Swagger documentation for the API by navigating to the following URL after running the application:

[http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

<div>
  <img src="https://github.com/user-attachments/assets/858d08e5-e7c2-4f05-addc-0c15b94a7a06" alt="documentation swagger" style="max-width: 550px;">
</div>
