package com.srms.dao;

import com.srms.model.Role;
import com.srms.model.User;
import com.srms.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation for user persistence and authentication lookups.
 */
public class UserDAO implements BaseDAO<User> {

    private static final String INSERT_SQL = """
            INSERT INTO users (username, password, role, is_active)
            VALUES (?, ?, ?, ?)
            """;

    private static final String SELECT_BY_ID_SQL = """
            SELECT user_id, username, password, role, is_active
            FROM users WHERE user_id = ?
            """;

    private static final String SELECT_BY_USERNAME_SQL = """
            SELECT user_id, username, password, role, is_active
            FROM users WHERE username = ?
            """;

    private static final String SELECT_ALL_SQL = """
            SELECT user_id, username, password, role, is_active
            FROM users ORDER BY username
            """;

    private static final String UPDATE_SQL = """
            UPDATE users
            SET username = ?, password = ?, role = ?, is_active = ?
            WHERE user_id = ?
            """;

    private static final String DELETE_SQL = "DELETE FROM users WHERE user_id = ?";

    @Override
    public void create(User user) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole().name());
            statement.setBoolean(4, user.isActive());
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setUserId(keys.getInt(1));
                }
            }
        }
    }

    @Override
    public User findById(String id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_ID_SQL)) {
            statement.setInt(1, Integer.parseInt(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        }
        return null;
    }

    public User findByUsername(String username) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_BY_USERNAME_SQL)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
            }
        }
        return null;
    }

    @Override
    public List<User> findAll() throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            return mapRows(resultSet);
        }
    }

    @Override
    public void update(User user) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getRole().name());
            statement.setBoolean(4, user.isActive());
            statement.setInt(5, user.getUserId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(String id) throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setInt(1, Integer.parseInt(id));
            statement.executeUpdate();
        }
    }

    private List<User> mapRows(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            users.add(mapRow(resultSet));
        }
        return users;
    }

    private User mapRow(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setUserId(resultSet.getInt("user_id"));
        user.setUsername(resultSet.getString("username"));
        user.setPassword(resultSet.getString("password"));
        user.setRole(Role.fromString(resultSet.getString("role")));
        user.setActive(resultSet.getBoolean("is_active"));
        return user;
    }
}
