package com.srms.service;

import com.srms.dao.UserDAO;
import com.srms.model.Role;
import com.srms.model.User;
import com.srms.util.PasswordUtil;
import com.srms.util.Validator;

import java.sql.SQLException;
import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();
    private final AuthService authService;

    public UserService(AuthService authService) {
        this.authService = authService;
    }

    public List<User> getAllUsers() throws SQLException {
        authService.requireAdmin();
        return userDAO.findAll();
    }

    public void createUser(User user, String plainPassword) throws SQLException {
        authService.requireAdmin();
        validateUser(user, plainPassword, true);

        if (userDAO.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }

        user.setPassword(PasswordUtil.hashPassword(plainPassword));
        userDAO.create(user);
    }

    public void updateUser(User user, String plainPassword) throws SQLException {
        authService.requireAdmin();
        validateUser(user, plainPassword, false);

        User existing = userDAO.findById(String.valueOf(user.getUserId()));
        if (existing == null) {
            throw new IllegalArgumentException("User not found.");
        }

        if (!existing.getUsername().equalsIgnoreCase(user.getUsername())
                && userDAO.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException("Username already exists.");
        }

        if (plainPassword != null && !plainPassword.isBlank()) {
            user.setPassword(PasswordUtil.hashPassword(plainPassword));
        } else {
            user.setPassword(existing.getPassword());
        }

        User current = authService.getCurrentUser();
        if (current != null && current.getUserId() == user.getUserId()) {
            if (user.getRole() != Role.ADMIN) {
                throw new IllegalArgumentException("You cannot remove your own administrator role.");
            }
            if (!user.isActive()) {
                throw new IllegalArgumentException("You cannot deactivate your own account.");
            }
        }

        userDAO.update(user);
    }

    public void setUserActive(int userId, boolean active) throws SQLException {
        authService.requireAdmin();
        User current = authService.getCurrentUser();
        if (!active && current != null && current.getUserId() == userId) {
            throw new IllegalArgumentException("You cannot deactivate your own account.");
        }
        User user = userDAO.findById(String.valueOf(userId));
        if (user == null) {
            throw new IllegalArgumentException("User not found.");
        }
        user.setActive(active);
        userDAO.update(user);
    }

    public void deleteUser(int userId) throws SQLException {
        authService.requireAdmin();
        User current = authService.getCurrentUser();
        if (current != null && current.getUserId() == userId) {
            throw new IllegalArgumentException("You cannot delete your own account while logged in.");
        }
        userDAO.delete(String.valueOf(userId));
    }

    private void validateUser(User user, String plainPassword, boolean isNew) {
        if (user == null) {
            throw new IllegalArgumentException("User data is required.");
        }
        Validator.validateUsername(user.getUsername());
        if (user.getRole() == null) {
            throw new IllegalArgumentException("Role is required.");
        }
        if (isNew) {
            Validator.validatePassword(plainPassword);
        } else if (plainPassword != null && !plainPassword.isBlank()) {
            Validator.validatePassword(plainPassword);
        }
    }
}
