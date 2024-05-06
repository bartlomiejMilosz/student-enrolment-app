# Student Enrollment App

## Description

The Student Enrollment App is a sophisticated Java Spring application designed to facilitate the management of student enrollments, book inventory, and rentals. It integrates seamlessly with PostgreSQL, leveraging Docker for easy setup and scalability. The application exposes several RESTful services, enabling users to interact with the system through well-defined endpoints for creating, updating, retrieving, and deleting records related to books, rentals, and students.

## Features

- Manage book inventory.
- Handle book rentals and returns.
- Enroll students and manage student information.
- Secure and scalable PostgreSQL database integration.

## In-Progress Features

- **Payment Module Integration**: Currently under development as indicated in the UML diagram, the payment module will handle different payment statuses such as paid, pending, and overdue. This will be crucial for managing financial transactions related to book rentals, ensuring a seamless and automated process for fee collection and status updates.

- **Course Enrollment**: Another feature in progress involves the course enrollment capabilities, which will allow students to enroll in courses directly through the app. This feature will include handling of various course levels such as beginner, intermediate, and advanced, providing a dynamic and flexible learning management system.

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- Docker
- Docker Compose

### Installation

1. Clone the repository:
   ```
   git clone [repository_url]
   ```
2. Navigate to the project directory:
   ```
   cd student-enrolment-app
   ```

### Running the Application

1. Build the application using Maven:
   ```
   mvn clean install
   ```
2. Start the application using Docker Compose:
   ```
   docker-compose up --build
   ```
   This command will set up the PostgreSQL database and run the application.

### Accessing the Application

Once the application is running, you can access it at:
```
http://localhost:8080
```

## API Endpoints

The application provides several RESTful endpoints:

### Books

- **POST /books**
    - Description: Creates a new book record.
    - Request Body: `BookDto`
    - Response: `201 Created`

- **GET /books**
    - Description: Retrieves a list of all books, with pagination.
    - Query Params: `page`, `size`
    - Response: `200 OK`

- **GET /books/{id}**
    - Description: Retrieves a specific book by its ID.
    - Response: `200 OK`

- **PUT /books/{id}**
    - Description: Updates a book record by ID.
    - Request Body: `BookDto`
    - Response: `200 OK`

- **DELETE /books/{id}**
    - Description: Deletes a specific book by its ID.
    - Response: `204 No Content`

### Rentals

- **POST /rentals**
    - Description: Creates a rental record for a book.
    - Request Body: `RentalDto`
    - Response: `201 Created`

- **PUT /rentals/{id}**
    - Description: Marks a book as returned.
    - Response: `200 OK`

### Students

- **POST /students**
    - Description: Enrolls a new student.
    - Request Body: `StudentDto`
    - Response: `201 Created`

- **GET /students**
    - Description: Retrieves a list of all students, with pagination.
    - Query Params: `page`, `size`
    - Response: `200 OK`

- **GET /students/{id}**
    - Description: Retrieves a specific student by ID.
    - Response: `200 OK`

- **PATCH /students/{id}**
    - Description: Partially updates a student record.
    - Request Body: `StudentDto`
    - Response: `200 OK`

- **DELETE /students/{id}**
    - Description: Deletes a specific student by ID.
    - Response: `204 No Content`

### Error Handling

- **GET /error**
    - Description: Redirects to custom error page.
    - Response: Forward to `/error.html`

## Contributing

Contributions to the Student Enrollment App are welcome. Please ensure that your commits adhere to the following guidelines:

- Keep descriptions clear and concise.
- Include comments in your code where necessary.
- Write tests for new features and ensure existing tests pass.
