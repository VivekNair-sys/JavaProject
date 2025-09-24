package edu.ccrm.domain;

public class Course {
    private final String code;
    private final String title;
    private final int credits;
    private final String instructor;
    private final Semester semester;
    private final String department;

    private Course(Builder b) {
        this.code = b.code;
        this.title = b.title;
        this.credits = b.credits;
        this.instructor = b.instructor;
        this.semester = b.semester;
        this.department = b.department;
    }

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public int getCredits() { return credits; }
    public String getInstructor() { return instructor; }
    public Semester getSemester() { return semester; }
    public String getDepartment() { return department; }

    @Override
    public String toString() { return code + " - " + title + " [" + credits + "cr]"; }

    public static class Builder {
        private final String code;
        private final String title;
        private int credits = 3;
        private String instructor = "TBD";
        private Semester semester = Semester.FALL;
        private String department = "GEN";

        public Builder(String code, String title) { this.code = code; this.title = title; }
        public Builder credits(int c){ this.credits = c; return this; }
        public Builder instructor(String i){ this.instructor = i; return this; }
        public Builder semester(Semester s){ this.semester = s; return this; }
        public Builder department(String d){ this.department = d; return this; }
        public Course build(){ return new Course(this); }
    }
}
