package Model;

public class User {
    private String name;
    private String email;
    private String password;

    public User(String name, String email, String password){
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    // Convert user to a line for saving in file
    public String toLine() {
        return name + "," + email + "," + password;
    }

    // Create user from a line in file
    public static User fromLine(String line) {
        String[] parts = line.split(",");
        if(parts.length < 3) return null;
        return new User(parts[0], parts[1], parts[2]);
    }
}
