# 🛍️ Quarkus Reactive Product Management API

This is a reactive microservice built with Quarkus to manage products via RESTful APIs. It supports full CRUD operations, stock availability checks, and price-based sorting.

## 🚀 Tech Stack

- Quarkus Reactive (RESTEasy Reactive)
- Hibernate REactive with Panache
- Postgres SQL database
- DTOs and validation
- OpenAPI (Swagger UI)
- JUnit + RestAssured for testing

## 📦 Features

- Create, Read, Update, Delete products
- Check stock availability (`/products/{id}/stock?count=10`)
- Get products sorted by price (`/products/sorted`)
- 85%+ test coverage for Resources using Quarkus test framework
- Additional endpoints created:
   - To Add Multiple Products at a time
   - To get the products based on range

## 🛠️ Setup Instructions

### Prerequisites

- Java 17+
- Maven 3.8+

### Run the App

🚀 Running the Application (Using STS + Maven Build)

This project uses Quarkus and is configured to run in dev mode via Spring Tool Suite (STS) using a Maven build configuration.

✅ Steps to Run the App
1. 	Build the project using STS:
• 	Right-click the project →  Run As → Run Configuration
• 	Select Maven Build → Click New Launch Configuration
• 	Goals: clean install
• 	Click Apply → then Run

2. 	Run the app in dev mode from STS:
• 	Right-click the project →  Run As → Run Configuration
• 	Select Maven Build → Click New Launch Configuration
• 	Goals: quarkus:dev
• 	Click Apply → then Run


🔗 Access API Documentation

Once running, open your browser:

• 	Swagger UI: http://localhost:9090/q/swagger-ui

• 	OpenAPI Spec (JSON): http://localhost:9090/q/openapi
