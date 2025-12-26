package Controller;

import Model.User;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import View.Registerframe;
import java.time.chrono.ThaiBuddhistEra;

public class UserController {

    private static final String FILE = "users.txt";

    // Register a new user
    public static String registerUser(String name, String email, String password, String confirmPassword) {
        if(name.isEmpty()) return "Name required";
        if(!email.contains("@")) return "Invalid email";
        if(!password.matches(".*[A-Z].*")){
        return "Password must contain at least one uppercase letter";
    }
    if(!password.matches(".*[a-z].*")){
        return "Password must contain at least one lowercase letter";
    }
    if(!password.matches(".*\\d.*")){
        return "Password must contain at least one number";
    }
    if(!password.matches(".*[!@#$%^&*()].*")){
        return "Password must contain at least one special character (!@#$%^&*())";
    }
        if(!password.equals(confirmPassword)) return "Passwords do not match";

        ArrayList<User> users = loadUsers();

        for(User u : users){
            if(u.getEmail().equals(email)) return "User already exists";
        }

        User newUser = new User(name, email, password);
        users.add(newUser);
        saveUsers(users);
        return "SUCCESS";
    }

    // Login user
public static User login(String email, String password) {
        ArrayList<User> users = loadUsers();
        for (User u : users) {
            if (u.getEmail().equals(email)) {
                if (u.getPassword().equals(password)) {
                    return u; // success
                } else {
                    return null; // password incorrect
                }
            }
        }
        return null; // email not found
    }


    // Load all users from file
    public static ArrayList<User> loadUsers(){
        ArrayList<User> users = new ArrayList<User>();
        try {
            File file = new File(FILE);
            if(!file.exists()) return users;

            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()){
                String line = sc.nextLine();
                User u = User.fromLine(line);
                if(u != null) users.add(u);
            }
            sc.close();
        } catch(Exception e){
            // ignore for simplicity
        }
        return users;
    }

    // Save all users to file
    public static void saveUsers(ArrayList<User> users){
        try {
            FileWriter writer = new FileWriter(FILE);
            for(User u : users){
                writer.write(u.toLine() + "\n");
            }
            writer.close();
        } catch(Exception e){
            // ignore for simplicity
        }
    }
    
    // Reset password for a user by email
public static String resetPassword(String email, String newPassword) {

    // Validation: password must be at least 8 characters
    if(newPassword.length() < 8){
        return "Password must be at least 8 characters";
    }

    // Optional: add more validations if you want
    // e.g., at least 1 uppercase, 1 number, 1 special character
    if(!newPassword.matches(".*[A-Z].*")){
        return "Password must contain at least one uppercase letter";
    }
    if(!newPassword.matches(".*[a-z].*")){
        return "Password must contain at least one lowercase letter";
    }
    if(!newPassword.matches(".*\\d.*")){
        return "Password must contain at least one number";
    }
    if(!newPassword.matches(".*[!@#$%^&*()].*")){
        return "Password must contain at least one special character (!@#$%^&*())";
    }

    // Load users
    ArrayList<User> users = loadUsers();
    boolean found = false;

    for (int i = 0; i < users.size(); i++) {
        User u = users.get(i);
        if (u.getEmail().equals(email)) {
            // update password
            users.set(i, new User(u.getName(), u.getEmail(), newPassword));
            found = true;
            break;
        }
    }

    if (found) {
        saveUsers(users); // save changes to file
        return "Password reset successful";
    } else {
        return "Email not found";
    }
}

}

    
  
