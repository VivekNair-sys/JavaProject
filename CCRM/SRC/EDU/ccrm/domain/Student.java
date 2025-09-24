package edu.ccrm.domain;

public class Student extends Person {
    private final String regNo;
    private boolean active = true;

    public Student(String id, String regNo, String fullName, String email) {
        super(id, fullName, email);
        this.regNo = regNo;
    }

    public String getRegNo() { return regNo; }
    public boolean isActive() { return active; }
    public void deactivate() { active = false; }

    @Override
    public String getRole() { return "Student"; }
}
