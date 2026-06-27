package com.srms.service;

import com.srms.dao.StudentDAO;
import com.srms.model.Student;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ReportService {

    private final StudentDAO studentDAO = new StudentDAO();

    public String buildSummaryReport() throws SQLException {
        List<Student> students = studentDAO.findAll();
        Map<String, Integer> byCourse = new LinkedHashMap<>();
        Map<Integer, Integer> byYear = new LinkedHashMap<>();

        for (Student student : students) {
            String course = student.getCourse();
            if (course == null || course.isBlank()) {
                course = "Unspecified";
            }
            byCourse.merge(course, 1, Integer::sum);
            byYear.merge(student.getYearLevel(), 1, Integer::sum);
        }

        StringBuilder report = new StringBuilder();
        report.append("STUDENT RECORD SUMMARY REPORT\n");
        report.append("=============================\n\n");
        report.append("Total Students: ").append(students.size()).append("\n\n");
        report.append("Students by Course:\n");
        for (Map.Entry<String, Integer> entry : byCourse.entrySet()) {
            report.append("  - ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        report.append("\nStudents by Year Level:\n");
        for (Map.Entry<Integer, Integer> entry : byYear.entrySet()) {
            report.append("  - Year ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        return report.toString();
    }
}
