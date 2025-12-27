package Controller;

import Model.User;
import Model.CurrentUser;

/**
 *
 * @author USER
 */
public class CurrentUserController {
    private CurrentUser currentUser;

    // Constructor accepting a User object

    /**
     *
     * @param user
     */
    public CurrentUserController(User user) {
        if (user != null) {
            this.currentUser = new CurrentUser(user.getName()); // or email if needed
        }
    }

    // Optional no-arg constructor

    /**
     *
     */
    public CurrentUserController() {
        this.currentUser = null;
    }

    // Login method (by username)

    /**
     *
     * @param username
     */
    public void login(String username) {
        currentUser = new CurrentUser(username);
    }

    // Logout method

    /**
     *
     */
    public void logout() {
        currentUser = null;
    }

    // Getter for current user

    /**
     *
     * @return
     */
    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    // Check if a user is logged in

    /**
     *
     * @return
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
