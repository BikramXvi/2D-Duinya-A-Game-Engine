package Model;

/**
 *
 * @author USER
 */
public class User {
    private String name;
    private String email;
    private String password;

    /**
     *
     * @param name
     * @param email
     * @param password
     */
    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    /**
     *
     * @return
     */
    public String getName() { return name; }

    /**
     *
     * @return
     */
    public String getEmail() { return email; }

    /**
     *
     * @return
     */
    public String getPassword() { return password; }

    // Convert user to a line for saving in file

    /**
     *
     * @return
     */
    public String toLine() {
        return name + "," + email + "," + password;
    }

    // Create user from a line in file

    /**
     *
     * @param line
     * @return
     */
    public static User fromLine(String line) {
        String[] parts = line.split(",");
        if(parts.length < 3) return null;
        return new User(parts[0], parts[1], parts[2]);
    }
}
