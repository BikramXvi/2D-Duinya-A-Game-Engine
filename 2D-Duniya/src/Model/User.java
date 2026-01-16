package Model;

/**
 * Represents a user with name, email, and password.
 */
public class User {
    // The user's name
    private String name;
    // The user's email
    private String email;
    // The user's password
    private String password;

    /**
     * Constructs a User with the given name, email, and password.
     * @param name the name
     * @param email the email
     * @param password the password
     */
    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     * Gets the name.
     * @return the name
     */
    public String getName() { return name; }

    /**
     * Gets the email.
     * @return the email
     */
    public String getEmail() { return email; }

    /**
     * Gets the password.
     * @return the password
     */
    public String getPassword() { return password; }

    /**
     * Converts the user to a comma-separated string for file storage.
     * @return the string representation
     */
    public String toLine() {
        return name + "," + email + "," + password;
    }

    /**
     * Creates a User from a comma-separated string.
     * @param line the string
     * @return the user, or null if invalid
     */
    public static User fromLine(String line) {
        String[] parts = line.split(",");
        if(parts.length < 3) return null;
        return new User(parts[0], parts[1], parts[2]);
    }
}
