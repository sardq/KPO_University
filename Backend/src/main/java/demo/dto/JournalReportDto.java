package demo.dto;

import java.time.LocalDateTime;
import java.util.List;

public class JournalReportDto {

    private String groupName;
    private String disciplineName;
    private String teacherName;
    private List<LocalDateTime> lessonDates;
    private List<StudentRow> students;
    private double groupAverage;
    private Long id;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDisciplineName() {
        return disciplineName;
    }

    public void setDisciplineName(String disciplineName) {
        this.disciplineName = disciplineName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public List<LocalDateTime> getLessonDates() {
        return lessonDates;
    }

    public void setLessonDates(List<LocalDateTime> lessonDates) {
        this.lessonDates = lessonDates;
    }

    public List<StudentRow> getStudents() {
        return students;
    }

    public void setStudents(List<StudentRow> students) {
        this.students = students;
    }

    public double getGroupAverage() {
        return groupAverage;
    }

    public void setGroupAverage(double groupAverage) {
        this.groupAverage = groupAverage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static class StudentRow {
        private Long studentId;
        private String studentName;
        private List<String> grades;
        private double average;

        public Long getStudentId() {
            return studentId;
        }

        public void setStudentId(Long studentId) {
            this.studentId = studentId;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public List<String> getGrades() {
            return grades;
        }

        public void setGrades(List<String> grades) {
            this.grades = grades;
        }

        public double getAverage() {
            return average;
        }

        public void setAverage(double average) {
            this.average = average;
        }
    }
}
