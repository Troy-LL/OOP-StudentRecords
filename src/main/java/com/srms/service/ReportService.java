package com.srms.service;

import com.srms.dao.StudentDAO;
import com.srms.model.Student;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aggregates student data into summary reports.
 */
public class ReportService {

    private final StudentDAO studentDAO = new StudentDAO();

    public int getTotalStudents() throws SQLException {
        return studentDAO.findAll().size();
    }

    public Map<String, Long> getStudentsByCourse() throws SQLException {
        return studentDAO.findAll().stream()
                .collect(Collectors.groupingBy(
                        student -> student.getCourse() == null || student.getCourse().isBlank()
                                ? "Unspecified" : student.getCourse(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    public Map<Integer, Long> getStudentsByYearLevel() throws SQLException {
        return studentDAO.findAll().stream()
                .collect(Collectors.groupingBy(Student::getYearLevel, LinkedHashMap::new, Collectors.counting()));
    }

    public String buildSummaryReport() throws SQLException {
        List<Student> students = studentDAO.findAll();
        StringBuilder report = new StringBuilder();
        report.append("STUDENT RECORD SUMMARY REPORT\n");
        report.append("=============================\n\n");
        report.append("Total Students: ").append(students.size()).append("\n\n");

        report.append("Students by Course:\n");
        getStudentsByCourse().forEach((course, count) ->
                report.append("  - ").append(course).append(": ").append(count).append("\n"));

        report.append("\nStudents by Year Level:\n");
        getStudentsByYearLevel().forEach((year, count) ->
                report.append("  - Year ").append(year).append(": ").append(count).append("\n"));

        return report.toString();
    }
}
