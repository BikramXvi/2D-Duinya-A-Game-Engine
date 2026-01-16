package Model;

/**
 * Represents the currently logged-in user with their username.
 */
public class CurrentUser {
    // The username of the current user
    private String username;

    /**
     * Constructs a CurrentUser with the given username.
     * @param username the username
     */
    public CurrentUser(String username) {
        this.username = username;
    }

    /**
     * Gets the username.
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     * @param username the new username
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
