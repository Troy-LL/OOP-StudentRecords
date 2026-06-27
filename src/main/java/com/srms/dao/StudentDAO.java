package com.srms.dao;

import com.srms.model.Student;
import com.srms.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation for student persistence.
 */
public class StudentDAO implements BaseDAO<Student> {

    private static final String INSERT_SQL = """
            INSERT INTO students (student_id, first_name, last_name, email, phone, course, year_level)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    private static final String SELECT_BY_ID_SQL = """
            SELECT student_id, first_name, last_name, email, phone, course, year_level, created_at
            FROM students WHERE student_id = ?
            """;

    private static final String SELECT_ALL_SQL = """
            SELECT student_id, first_name, last_name, email, phone, course, year_level, created_at
            FROM students ORDER BY last_name, first_name
            """;

    private static final String SEARCH_SQL = """
            SELECT student_id, first_name, last_name, email, phone, course, year_level, created_at
            FROM students
            WHERE student_id LIKE ? OR first_name LIKE ? OR last_name LIKE ?
            ORDER BY last_name, first_name
            """;

    private static final String UPDATE_SQL = """
            UPDATE students
            SET first_name = ?, last_name = ?, email = ?, phone = ?, course = ?, year_level = ?
            WHERE student_id = ?
            """;

    private static final String DELETE_SQL = "DELETE FROM students WHERE student_id = ?";

    @Override
    public void create(Student student) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            bindStudent(statement, student);
            statement.executeUpdate();
        }
    }

    @Override
    public Student findById(String id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public List<Student> findAll() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            return mapRows(resultSet);
        }
    }

    public List<Student> search(String keyword) throws SQLException {
        String pattern = "%" + keyword.trim() + "%";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SEARCH_SQL)) {
            statement.setString(1, pattern);
            statement.setString(2, pattern);
            statement.setString(3, pattern);
            try (ResultSet resultSet = statement.executeQuery()) {
                return mapRows(resultSet);
            }
        }
    }

    @Override
    public void update(Student student) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, student.getFirstName());
            statement.setString(2, student.getLastName());
            statement.setString(3, student.getEmail());
            statement.setString(4, student.getPhone());
            statement.setString(5, student.getCourse());
            statement.setInt(6, student.getYearLevel());
            statement.setString(7, student.getStudentId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setString(1, id);
            statement.executeUpdate();
        }
    }

    private void bindStudent(PreparedStatement statement, Student student) throws SQLException {
        statement.setString(1, student.getStudentId());
        statement.setString(2, student.getFirstName());
        statement.setString(3, student.getLastName());
        statement.setString(4, student.getEmail());
        statement.setString(5, student.getPhone());
        statement.setString(6, student.getCourse());
        statement.setInt(7, student.getYearLevel());
    }

    private List<Student> mapRows(ResultSet resultSet) throws SQLException {
        List<Student> students = new ArrayList<>();
        while (resultSet.next()) {
            students.add(mapRow(resultSet));
        }
        return students;
    }

    private Student mapRow(ResultSet resultSet) throws SQLException {
        Student student = new Student();
        student.setStudentId(resultSet.getString("student_id"));
        student.setFirstName(resultSet.getString("first_name"));
        student.setLastName(resultSet.getString("last_name"));
        student.setEmail(resultSet.getString("email"));
        student.setPhone(resultSet.getString("phone"));
        student.setCourse(resultSet.getString("course"));
        student.setYearLevel(resultSet.getInt("year_level"));
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            student.setCreatedAt(createdAt.toLocalDateTime());
        }
        return student;
    }
}
