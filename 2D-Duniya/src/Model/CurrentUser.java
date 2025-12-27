package Model;

/**
 *
 * @author USER
 */
public class CurrentUser {
    private String username;

    /**
     *
     * @param username
     */
    public CurrentUser(String username) {
        this.username = username;
    }

    /**
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
