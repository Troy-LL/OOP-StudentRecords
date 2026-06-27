package com.srms.service;

import com.srms.dao.UserDAO;
import com.srms.model.User;
import com.srms.util.PasswordUtil;
import com.srms.util.Validator;

import java.sql.SQLException;

/**
 * Authentication and session management service.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private User currentUser;

    public User login(String username, String password) throws SQLException {
        Validator.validateUsername(username);
        Validator.requireNonBlank(password, "Password");

        User user = userDAO.findByUsername(username.trim());
        if (user == null) {
            throw new IllegalArgumentException("Invalid username or password.");
        }
        if (!user.isActive()) {
            throw new IllegalArgumentException("This account is deactivated. Contact an administrator.");
        }
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password.");
        }

        currentUser = user;
        return user;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public void requireAdmin() {
        if (!isAdmin()) {
            throw new SecurityException("Administrator access is required for this action.");
        }
    }
}
