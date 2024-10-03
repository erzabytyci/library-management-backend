Library Management System - Backend
This is the backend for the Library Management System, developed using Java and Spring Boot. It provides the REST API endpoints for managing books, users, borrowing history, and user authentication.

Features:
-User Registration and Authentication using JWT.
-Role-based Access Control (Admin and User roles).
-CRUD Operations for books and users.
-Borrow and Return Books.
-Borrowing History tracking.
-Pagination and Filtering for books.

Technologies Used:
-Java 21
-Spring Boot 3
-Spring Security
-JWT Authentication
-MySQL (Database)
-Hibernate/JPA (Data Persistence)
-Maven (Dependency Management)

Requirements:
-Java 21 or higher installed.
-MySQL database installed and running.
-Maven installed for building the project.
-Postman or similar tool for testing API endpoints.

Getting Started
1. Clone the repository
Copy code
git clone -- https://github.com/erzabytyci/library-management-backend.git
cd library-management-backend
2. Configure MySQL Database
Make sure to update the application.properties file with your MySQL configuration!

properties
Copy code
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=update

3. Run the Application
To build and run the application:

Copy code
mvn clean install
mvn spring-boot:run
The server will start at http://localhost:8080.

4. Testing the API
You can test the API using Postman or any other API testing tool. Here are the main API endpoints:

POST /api/authenticate – Authenticate a user and obtain a JWT token.
POST /api/users/register – Register a new user.
GET /api/books – Get list of books.
GET /api/books/filter?title=Poem in Action&available=true - Get filtered list of books.
GET /api/books/paginated?page=0&size=1 - Get a paginated list of books.
POST /api/books - Add a new book (Admin).
PUT /api/books/{bookId}/borrow/{userId} – Borrow a book.
PUT /api/books/{bookId}/return/{userId} – Return a book.
GET /api/borrowing-history/{userId} – Get borrowing history for a user.
GET /api/borrowing-history – Get borrowing history for all users (Admin).
DELETE /api/books/{bookId} - Delete a book (Admin).
DELETE /api/users/{userId} - Delete a user (Admin).

5. JWT Authentication
To access protected endpoints, you need to include the JWT token in the Authorization header:

makefile
Authorization: Bearer <your-token>
You can obtain the token by authenticating through the /api/authenticate endpoint.

6. Roles and Permissions
Admin: Can view and manage all books and users, view all borrowing histories.
User: Can borrow and return books, view their own borrowing history.

Project Structure:

src
├── main
│   ├── java
│   │   └── com.example.librarymanagement
|   |       ├── config            # Security Configuration
│   │       ├── controller        # REST Controllers
│   │       ├── entity            # JPA Entities
│   │       ├── jwtfilter         # Security Configuration (JWT)
│   │       ├── repository        # Data Repositories
│   │       └── service           # Business Logic
│   └── resources
│       ├── application.properties # Application Configuration
└── test
    └── java                      # Unit and Integration Tests

License
This project is licensed under the MIT License - see the LICENSE file for details.
