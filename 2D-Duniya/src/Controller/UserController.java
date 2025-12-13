package Controller;

import Model.User;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.ArrayList;

public class UserController {

    private static final String FILE = "users.txt";

    // Register a new user
    public static String registerUser(String name, String email, String password, String confirmPassword) {
        if(name.isEmpty()) return "Name required";
        if(!email.contains("@")) return "Invalid email";
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
    public static boolean login(String email, String password){
        ArrayList<User> users = loadUsers();
        for(User u : users){
            if(u.getEmail().equals(email) && u.getPassword().equals(password)) return true;
        }
        return false;
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
}
