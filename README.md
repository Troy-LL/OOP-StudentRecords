# Student Record Management System

Java Swing desktop application for managing student records with MySQL, implementing OOP principles and JDBC connectivity.

## Screenshots

### Login

![Login screen](program%20screenshots/login.png)

### Main Menu

![Main menu](program%20screenshots/main-menu.png)

### Student Records

![Student records list with search, update, and delete](program%20screenshots/student-records.png)

### Add Student

![Add student form](program%20screenshots/add-student.png)

### Reports

![Student summary report](program%20screenshots/reports.png)

### User Management (Admin)

![User management screen](program%20screenshots/user-management.png)

## Prerequisites

- Java 17 or later
- Maven 3.8+
- MySQL 8.x (already installed; the setup script handles the rest)

## Quick Start (Windows, one click)

If your machine already has MySQL, Java 17, and Maven installed, just:

1. Double-click **`Setup-And-Run.bat`** and approve the Administrator prompt.

That's it. The script automatically:

- finds and starts MySQL,
- resets the `root` password to `Root@1234` (only if it doesn't already match),
- creates the `srms_db` database and seeds the sample data,
- builds the project and launches the app.

Administrator rights are required because starting the MySQL service and
resetting the password are privileged operations.

## Manual Setup

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
program screenshots/
```

## Features

- Secure login with role-based access (Admin, Staff)
- Student CRUD with search by ID or name
- Input validation and exception handling
- Summary reports by course and year level
- Administrator user management
