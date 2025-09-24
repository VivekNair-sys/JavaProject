package edu.ccrm.domain;

import java.time.LocalDate;

public class Enrollment {
    private final String studentId;
    private final String courseCode;
    private int marks = -1;
    private Grade grade = Grade.F;
    private final LocalDate enrolledOn;

    public Enrollment(String studentId, String courseCode) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.enrolledOn = LocalDate.now();
    }

    public String getStudentId() { return studentId; }
    public String getCourseCode() { return courseCode; }
    public Integer getMarks() { return marks < 0 ? null : marks; }
    public Grade getGrade() { return grade; }
    public void setMarks(int marks) {
        this.marks = marks;
        this.grade = Grade.fromScore(marks);
    }

    @Override
    public String toString() {
        return String.format("%s in %s => marks=%s grade=%s", studentId, courseCode, (marks < 0 ? "N/A" : marks), grade);
    }
}
