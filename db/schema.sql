-- Student Record Management System database schema
-- Default admin credentials: username=admin, password=admin123

CREATE DATABASE IF NOT EXISTS srms_db;
USE srms_db;

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'STAFF') NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS students (
    student_id VARCHAR(20) PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(20),
    course VARCHAR(100),
    year_level INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Password hash for admin123 (SHA-256 with embedded salt via PasswordUtil)
INSERT INTO users (username, password, role, is_active)
SELECT 'admin', 'srms2024salt!!:xJht8ivLykUnRsAsFv3uCB80pKjbEJ4ote5nfJx6ius=', 'ADMIN', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (username, password, role, is_active)
SELECT 'staff', 'srms2024salt!!:xJht8ivLykUnRsAsFv3uCB80pKjbEJ4ote5nfJx6ius=', 'STAFF', TRUE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'staff');

INSERT INTO students (student_id, first_name, last_name, email, phone, course, year_level)
SELECT '2024-0001', 'Juan', 'Dela Cruz', 'juan.delacruz@school.edu', '09171234567', 'BS Computer Science', 2
WHERE NOT EXISTS (SELECT 1 FROM students WHERE student_id = '2024-0001');

INSERT INTO students (student_id, first_name, last_name, email, phone, course, year_level)
SELECT '2024-0002', 'Maria', 'Santos', 'maria.santos@school.edu', '09179876543', 'BS Information Technology', 3
WHERE NOT EXISTS (SELECT 1 FROM students WHERE student_id = '2024-0002');

INSERT INTO students (student_id, first_name, last_name, email, phone, course, year_level)
SELECT '2021-00481-MN-0', 'Aleckz Danielle', 'Agojo', 'aleckz.agojo@iskolarngbayan.edu.ph', '09171000001', 'BS Computer Science', 3
WHERE NOT EXISTS (SELECT 1 FROM students WHERE student_id = '2021-00481-MN-0');

INSERT INTO students (student_id, first_name, last_name, email, phone, course, year_level)
SELECT '2021-00522-MN-0', 'Mhyka Gabrielle', 'Gonzalez', 'mhyka.gonzalez@iskolarngbayan.edu.ph', '09171000002', 'BS Computer Science', 3
WHERE NOT EXISTS (SELECT 1 FROM students WHERE student_id = '2021-00522-MN-0');

INSERT INTO students (student_id, first_name, last_name, email, phone, course, year_level)
SELECT '2021-00637-MN-0', 'Jisselle Vica', 'Somera', 'jisselle.somera@iskolarngbayan.edu.ph', '09171000003', 'BS Computer Science', 3
WHERE NOT EXISTS (SELECT 1 FROM students WHERE student_id = '2021-00637-MN-0');

INSERT INTO students (student_id, first_name, last_name, email, phone, course, year_level)
SELECT '2021-00748-MN-0', 'Diorene Mae', 'Moralde', 'diorene.moralde@iskolarngbayan.edu.ph', '09171000004', 'BS Computer Science', 3
WHERE NOT EXISTS (SELECT 1 FROM students WHERE student_id = '2021-00748-MN-0');

INSERT INTO students (student_id, first_name, last_name, email, phone, course, year_level)
SELECT '2021-00859-MN-0', 'Troy Lauren', 'Lazaro', 'troy.lazaro@iskolarngbayan.edu.ph', '09171000005', 'BS Computer Science', 3
WHERE NOT EXISTS (SELECT 1 FROM students WHERE student_id = '2021-00859-MN-0');
