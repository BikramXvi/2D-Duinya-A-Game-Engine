package Controller;

import Model.CurrentUser;
import Model.User;

/**
 * Manages the current logged-in user, providing login, logout, and user retrieval functionality.
 */
public class CurrentUserController {
    // The current logged-in user instance
    private CurrentUser currentUser;

    /**
     * Constructs a CurrentUserController with the given user, initializing the current user.
     * @param user the user to set as current
     */
    public CurrentUserController(User user) {
        if (user != null) {
            this.currentUser = new CurrentUser(user.getName()); // or email if needed
        }
    }

    /**
     * Constructs a CurrentUserController with no current user.
     */
    public CurrentUserController() {
        this.currentUser = null;
    }

    /**
     * Logs in the user with the given username.
     * @param username the username of the user to log in
     */
    public void login(String username) {
        currentUser = new CurrentUser(username);
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Gets the current logged-in user.
     * @return the current user, or null if not logged in
     */
    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if a user is currently logged in.
     * @return true if logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
