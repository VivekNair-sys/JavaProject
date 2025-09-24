package edu.ccrm.domain;

import java.time.LocalDate;

public abstract class Person {
    protected final String id;
    protected String fullName;
    protected String email;
    protected final LocalDate createdOn;

    public Person(String id, String fullName, String email) {
        assert id != null;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.createdOn = LocalDate.now();
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public LocalDate getCreatedOn() { return createdOn; }

    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("%s[id=%s,name=%s,email=%s]", getRole(), id, fullName, email);
    }
}
