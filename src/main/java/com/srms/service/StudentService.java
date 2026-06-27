package com.srms.service;

import com.srms.dao.StudentDAO;
import com.srms.model.Student;
import com.srms.util.Validator;

import java.sql.SQLException;
import java.util.List;

/**
 * Business logic for student record management.
 */
public class StudentService {

    private final StudentDAO studentDAO = new StudentDAO();

    public void addStudent(Student student) throws SQLException {
        validateStudent(student);
        if (studentDAO.findById(student.getStudentId()) != null) {
            throw new IllegalArgumentException("Student ID already exists.");
        }
        studentDAO.create(student);
    }

    public List<Student> getAllStudents() throws SQLException {
        return studentDAO.findAll();
    }

    public List<Student> searchStudents(String keyword) throws SQLException {
        Validator.requireNonBlank(keyword, "Search keyword");
        return studentDAO.search(keyword.trim());
    }

    public Student getStudent(String studentId) throws SQLException {
        Validator.requireNonBlank(studentId, "Student ID");
        Student student = studentDAO.findById(studentId.trim());
        if (student == null) {
            throw new IllegalArgumentException("Student not found.");
        }
        return student;
    }

    public void updateStudent(Student student) throws SQLException {
        validateStudent(student);
        if (studentDAO.findById(student.getStudentId()) == null) {
            throw new IllegalArgumentException("Student not found.");
        }
        studentDAO.update(student);
    }

    public void deleteStudent(String studentId) throws SQLException {
        Validator.requireNonBlank(studentId, "Student ID");
        if (studentDAO.findById(studentId.trim()) == null) {
            throw new IllegalArgumentException("Student not found.");
        }
        studentDAO.delete(studentId.trim());
    }

    private void validateStudent(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student data is required.");
        }
        Validator.requireNonBlank(student.getStudentId(), "Student ID");
        Validator.requireNonBlank(student.getFirstName(), "First name");
        Validator.requireNonBlank(student.getLastName(), "Last name");
        Validator.validateEmail(student.getEmail());
        Validator.validatePhone(student.getPhone());
        Validator.requireNonBlank(student.getCourse(), "Course");
        Validator.validateYearLevel(student.getYearLevel());
    }
}
