package com.sams.util;

import com.sams.model.User;

/**
 * Session utility to keep track of the currently logged-in user.
 */
public class Session {

    private static User currentUser;

    private Session() {
        // Prevent instantiation
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    public static boolean isLecturer() {
        return currentUser != null && currentUser.isLecturer();
    }

    /**
     * Clears the current user session on logout.
     */
    public static void clear() {
        currentUser = null;
    }
}
