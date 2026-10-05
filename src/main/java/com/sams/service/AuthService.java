package com.sams.service;

import com.sams.dao.UserDAO;
import com.sams.model.User;
import com.sams.util.Session;

import java.sql.SQLException;

/**
 * AuthService – handles business logic for authentication.
 * Mediates between controllers and the UserDAO.
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAO();
    }

    public AuthService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    /**
     * Authenticates a user with given credentials.
     * If authentication succeeds, sets the current session user.
     *
     * @param username the username
     * @param password the password
     * @return the logged-in User, or null if credentials are invalid
     * @throws IllegalArgumentException if username or password is blank
     * @throws SQLException if a database error occurs
     */
    public User login(String username, String password) throws SQLException {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }

        User user = userDAO.login(username.trim(), password);
        if (user != null) {
            Session.setCurrentUser(user);
        }
        return user;
    }

    /**
     * Logs out the current user by clearing the session.
     */
    public void logout() {
        Session.clear();
    }
}
