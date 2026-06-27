# Student Record Management System

Java Swing desktop application for managing student records with MySQL, implementing OOP principles and JDBC connectivity.

## Prerequisites

- Java 17 or later
- Maven 3.8+
- MySQL 8.x

## Setup

1. Update database credentials in `src/main/resources/db.properties`.
2. Create the database and seed data:

```sql
source db/schema.sql
```

3. Build and run:

```bash
mvn clean package
java -jar target/student-record-management-system-1.0.0.jar
```

Or run from your IDE with main class `com.srms.app.Main`.

## Default Accounts

| Username | Password | Role |
| --- | --- | --- |
| admin | admin123 | ADMIN |
| staff | admin123 | STAFF |

## Project Structure

```
src/main/java/com/srms/
  app/       Application entry point
  model/     Person, Student, User, Role
  dao/       JDBC data access (BaseDAO, StudentDAO, UserDAO)
  service/   Business logic and validation
  ui/        Swing frames and dialogs
  util/      DB connection, password hashing, validation
db/schema.sql
```

## Features

- Secure login with role-based access (Admin, Staff)
- Student CRUD with search by ID or name
- Input validation and exception handling
- Summary reports by course and year level
- Administrator user management
