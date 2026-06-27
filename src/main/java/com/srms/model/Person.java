package com.srms.model;

/**
 * Abstract base class demonstrating inheritance and encapsulation for person entities.
 */
public abstract class Person {

    private String firstName;
    private String lastName;

    protected Person() {
    }

    protected Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
    }

    /** Polymorphic display label for UI and reports. */
    public abstract String getDisplayLabel();

    @Override
    public String toString() {
        return getDisplayLabel();
    }
}
