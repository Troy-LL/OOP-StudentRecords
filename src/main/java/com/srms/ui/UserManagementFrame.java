package com.srms.ui;

import com.srms.model.Role;
import com.srms.model.User;
import com.srms.service.UserService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * Administrator-only user account management screen.
 */
public class UserManagementFrame extends JFrame {

    private final UserService userService;
    private final DefaultTableModel tableModel;
    private final JTable table = new JTable();

    public UserManagementFrame(Frame owner, UserService userService) {
        this.userService = userService;
        this.tableModel = new DefaultTableModel(
                new String[]{"User ID", "Username", "Role", "Active"},
                0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        initialize(owner);
        loadUsers();
    }

    private void initialize(Frame owner) {
        setTitle("User Management");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addButton = new JButton("Add User");
        JButton editButton = new JButton("Edit User");
        JButton activateButton = new JButton("Activate");
        JButton deactivateButton = new JButton("Deactivate");
        JButton deleteButton = new JButton("Delete");

        addButton.addActionListener(e -> addUser());
        editButton.addActionListener(e -> editUser());
        activateButton.addActionListener(e -> setActive(true));
        deactivateButton.addActionListener(e -> setActive(false));
        deleteButton.addActionListener(e -> deleteUser());

        actions.add(addButton);
        actions.add(editButton);
        actions.add(activateButton);
        actions.add(deactivateButton);
        actions.add(deleteButton);
        actions.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(actions, BorderLayout.SOUTH);

        setSize(700, 400);
        setLocationRelativeTo(owner);
    }

    private void loadUsers() {
        try {
            List<User> users = userService.getAllUsers();
            tableModel.setRowCount(0);
            for (User user : users) {
                tableModel.addRow(new Object[]{
                        user.getUserId(),
                        user.getUsername(),
                        user.getRole(),
                        user.isActive() ? "Yes" : "No"
                });
            }
        } catch (SecurityException ex) {
            UiUtil.showError(this, ex.getMessage());
            dispose();
        } catch (SQLException ex) {
            UiUtil.showError(this, "Failed to load users: " + ex.getMessage());
        }
    }

    private void addUser() {
        UserFormDialog dialog = new UserFormDialog(this, null);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            try {
                userService.createUser(dialog.getUser(), dialog.getPlainPassword());
                UiUtil.showInfo(this, "User created successfully.");
                loadUsers();
            } catch (IllegalArgumentException | SecurityException ex) {
                UiUtil.showError(this, ex.getMessage());
            } catch (SQLException ex) {
                UiUtil.showError(this, "Failed to create user: " + ex.getMessage());
            }
        }
    }

    private void editUser() {
        User selected = getSelectedUser();
        if (selected == null) {
            return;
        }
        UserFormDialog dialog = new UserFormDialog(this, selected);
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            try {
                userService.updateUser(dialog.getUser(), dialog.getPlainPassword());
                UiUtil.showInfo(this, "User updated successfully.");
                loadUsers();
            } catch (IllegalArgumentException | SecurityException ex) {
                UiUtil.showError(this, ex.getMessage());
            } catch (SQLException ex) {
                UiUtil.showError(this, "Failed to update user: " + ex.getMessage());
            }
        }
    }

    private void setActive(boolean active) {
        User selected = getSelectedUser();
        if (selected == null) {
            return;
        }
        try {
            userService.setUserActive(selected.getUserId(), active);
            UiUtil.showInfo(this, "User status updated.");
            loadUsers();
        } catch (IllegalArgumentException | SecurityException ex) {
            UiUtil.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            UiUtil.showError(this, "Failed to update user status: " + ex.getMessage());
        }
    }

    private void deleteUser() {
        User selected = getSelectedUser();
        if (selected == null) {
            return;
        }
        if (!UiUtil.confirm(this, "Delete user " + selected.getUsername() + "?")) {
            return;
        }
        try {
            userService.deleteUser(selected.getUserId());
            UiUtil.showInfo(this, "User deleted.");
            loadUsers();
        } catch (IllegalArgumentException | SecurityException ex) {
            UiUtil.showError(this, ex.getMessage());
        } catch (SQLException ex) {
            UiUtil.showError(this, "Failed to delete user: " + ex.getMessage());
        }
    }

    private User getSelectedUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            UiUtil.showError(this, "Select a user first.");
            return null;
        }
        User user = new User();
        user.setUserId((Integer) tableModel.getValueAt(row, 0));
        user.setUsername(String.valueOf(tableModel.getValueAt(row, 1)));
        user.setRole(Role.fromString(String.valueOf(tableModel.getValueAt(row, 2))));
        user.setActive("Yes".equals(tableModel.getValueAt(row, 3)));
        return user;
    }

    private static class UserFormDialog extends JDialog {
        private final JTextField usernameField = new JTextField(20);
        private final JPasswordField passwordField = new JPasswordField(20);
        private final JComboBox<Role> roleComboBox = new JComboBox<>(Role.values());
        private final JCheckBox activeCheckBox = new JCheckBox("Active", true);
        private boolean saved;
        private User user;

        UserFormDialog(Frame owner, User existing) {
            super(owner, existing == null ? "Add User" : "Edit User", true);
            this.user = existing == null ? new User() : existing;
            initialize(existing != null);
        }

        private void initialize(boolean editing) {
            setLayout(new BorderLayout(8, 8));

            JPanel form = UiUtil.formPanel();
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.insets = new Insets(4, 4, 4, 4);
            constraints.anchor = GridBagConstraints.WEST;

            UiUtil.addFormRow(form, constraints, 0, "Username:", usernameField);
            UiUtil.addFormRow(form, constraints, 1,
                    editing ? "New Password (optional):" : "Password:", passwordField);
            UiUtil.addFormRow(form, constraints, 2, "Role:", roleComboBox);
            UiUtil.addFormRow(form, constraints, 3, "", activeCheckBox);

            if (editing) {
                usernameField.setText(user.getUsername());
                roleComboBox.setSelectedItem(user.getRole());
                activeCheckBox.setSelected(user.isActive());
            }

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton saveButton = new JButton("Save");
            JButton cancelButton = new JButton("Cancel");
            saveButton.addActionListener(e -> save());
            cancelButton.addActionListener(e -> dispose());
            buttons.add(saveButton);
            buttons.add(cancelButton);

            add(form, BorderLayout.CENTER);
            add(buttons, BorderLayout.SOUTH);
            pack();
            setLocationRelativeTo(getOwner());
        }

        private void save() {
            user.setUsername(usernameField.getText().trim());
            user.setRole((Role) roleComboBox.getSelectedItem());
            user.setActive(activeCheckBox.isSelected());
            saved = true;
            dispose();
        }

        boolean isSaved() {
            return saved;
        }

        User getUser() {
            return user;
        }

        String getPlainPassword() {
            return new String(passwordField.getPassword());
        }
    }
}
