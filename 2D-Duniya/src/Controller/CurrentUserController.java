package Controller;

import Model.User;
import Model.CurrentUser;

public class CurrentUserController {
    private CurrentUser currentUser;

    // Constructor accepting a User object
    public CurrentUserController(User user) {
        if (user != null) {
            // Wrap the User into CurrentUser
            this.currentUser = new CurrentUser(user.getName()); // or email if needed
        }
    }

    // Optional no-arg constructor
    public CurrentUserController() {
        this.currentUser = null;
    }

    // Login method (by username)
    public void login(String username) {
        currentUser = new CurrentUser(username);
    }

    // Logout method
    public void logout() {
        currentUser = null;
    }

    // Getter for current user
    public CurrentUser getCurrentUser() {
        return currentUser;
    }

    // Check if a user is logged in
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
