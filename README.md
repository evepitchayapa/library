## Library Management System
Description
A Spring Boot-based Library Management System that provides a simple API for managing books. This system supports operations such as adding and searching by author. 
The application uses Spring Data JPA and MySQL for data persistence.


## Features
- Add books in the library.
- Search for books based on author.
- Integration with MySQL using Spring Data JPA.


## Prerequisites
- Java 11 or later
- MySQL 5.7 or later
- Maven 
- IDE like IntelliJ IDEA or Eclipse (optional)


## Installation
### Step 1: Clone the Repository
Clone the repository to your local machine:
``` 
git clone https://github.com/evepitchayapa/library.git 
```

### Step 2: Set Up MySQL Database
Create a database in MySQL:
``` 
CREATE SCHEMA IF NOT EXISTS Library;
USE Library;

DROP TABLE IF EXISTS books;

CREATE TABLE books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    published_date DATE
);
```

### Step 3: Configure Application Properties
```
spring.datasource.url=jdbc:mysql://localhost:3306/library_db
spring.datasource.username=library_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true
```

### Step 4: Install Dependencies
```
mvn clean install
```

### Step 5: Run the Application
To run the application, use the following Maven command:
```
mvn spring-boot:run
```


## Running Integration Tests
### Using Maven:
To run all tests (including integration tests):
```
mvn test
```


## API Endpoints
- GET /api/books?author=?: Get a book searching by author
- POST /api/books: Add a new book (provide book details in the request body).
    - title (String datatype) is require
    - author (String datatype) is require
    - publishedDate ** accept publishedDate in Buddhist calendar


## Example API Requests and Expected Responses
### 1. GET /api/books - Get Books by Author
Request:
```
curl -X GET "http://localhost:8080/api/books?author=Author1"
```
Expected Response (Books Found):
```
{
  "status": "success",
  "data": [
    {
      "id": 1,
      "title": "Book 1",
      "author": "Author1",
      "publishedDate": "2023-01-01"
    },
    {
      "id": 2,
      "title": "Book 2",
      "author": "Author1",
      "publishedDate": "2022-05-05"
    }
  ]
}
```
### 2. GET /api/books - Get Books by Author (No Books Found)
Request:
```
curl -X GET "http://localhost:8080/api/books?author=Unknown Author"
```
Expected Response (Books not Found):
```
{
  "status": "error",
  "message": "No books found for this author"
}
```
### 3. POST /api/books - Save New Book
Request:
```
curl -X POST "http://localhost:8080/api/books" \
  -H "Content-Type: application/json" \
  -d '{
        "title": "New Book",
        "author": "Author1",
        "publishedDate": "2023-01-01"
      }'
```
Expected Response (Save Book success):
```
{
  "status": "success",
  "message": "Book saved successfully",
  "data": {
    "id": 1,
    "title": "New Book",
    "author": "Author1",
    "publishedDate": "2023-01-01"
  }
}
```
### 4. POST /api/books - Validation Errors for Empty Fields
Request:
```
curl -X POST "http://localhost:8080/api/books" \
  -H "Content-Type: application/json" \
  -d '{
        "title": "",
        "author": "",
        "publishedDate": "2023-01-01"
      }'

```
Expected Response (Validation Failed):
```
{
  "status": "error",
  "message": "Validation failed",
  "data": {
    "title": "Title is required",
    "author": "Author is required"
  }
}
```






