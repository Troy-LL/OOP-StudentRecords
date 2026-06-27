package com.srms.model;

/**
 * Application user entity mapped to the users table.
 */
public class User extends Person {

    private int userId;
    private String username;
    private String password;
    private Role role;
    private boolean active;

    public User() {
    }

    public User(int userId, String username, String password, Role role, boolean active) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.active = active;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    @Override
    public String getDisplayLabel() {
        return username + " (" + role + ")";
    }
}
