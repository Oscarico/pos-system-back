# POS System Backend

REST API for a Point of Sale (POS) system built with Java, Spring Boot, and PostgreSQL.

## 🚀 Features

- *RESTful API for POS operations.*
- *Entity-based archictecture*
- *DTO pattern for request and response data.*
- *Exception handling.*
- *Entity mapping.*
- *Database persistence with PostgreSQL.*
- *Authentication and authorization using Spring Security.*


## 📁 Project Structure

- **src:** Java files.
- **src/config:** Aplication configuration.
- **src/controller:** REST Controllers.
- **src/dto:** Data Transfer Objects.
- **src/entity:** Database Entities.
- **src/enums:** Enumerations used by the system.
- **src/exception/** Custom Exceptions and exceptions handling.
- **src/mapper** Entity and DTO mappers.
- **src/payload** Request and response payloads.
- **src/repository** Data access repositories.
- **src/security** Security and authentication configuration.
- **src/service** Business logic.

## 🛠️ Tech Stack

| Technology      | Purpose                              |
| :-------------- | :----------------------------------- | 
| Java            | PostgreSql                           | 
| Spring Boot     | Backend framework                    |
| Spring Security | Authentication and authorization     |
| PostegreSQL     | Database                             |
| Maven           | Dependency management and build tool |

## ⚙️ Requirements

- *Java 17+*
- *Maven*
- *PostgreSQL*

## ▶️ Getting Started

- **1. Clone the repository**
- git clone <repository-url>
- cd <repository-folder>

- **2. Configure PostgreSQL**
- Create a PostgreSQL database and configure the database connection in the application properties.
- spring.datasource.url=jdbc:postgresql://localhost:5432/<database-name>
- spring.datasource.username=<username>
- spring.datasource.password=<password>

- **3. Run the application**
- Using Maven:
- ./mvnw spring-boot:run
- Or on Windows:
- mvnw.cmd spring-boot:run

- **The API will be available at:**
- http://localhost:8080

## 📌 Status

**This project is currently under development.**