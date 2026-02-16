# Orders Service

Orders Service is a microservice responsible for **managing and processing customer orders** in a marketplace microservices architecture.

---

## Tech Stack

- Java 21+ / Spring Boot  
- Spring Data JPA  
- PostgreSQL  
- Spring Security + JWT  
- Validation (DTO validation)  
- Lombok  

---

## Features

- Create, read, update, and delete orders  
- Connects with Product Service to check stock availability  
- JWT-based authentication (trusts Users Service tokens)  
- Role-based access control (USER / ADMIN)  
- REST API endpoints for order management  

---

## Getting Started

### Prerequisites

- Java 21+  
- PostgreSQL running locally  
- Gradle  

### Database Setup

Create a database for the service:

```bash
createdb market_orders_db
