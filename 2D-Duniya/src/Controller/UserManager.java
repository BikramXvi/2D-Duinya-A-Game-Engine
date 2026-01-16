package Controller;

import Model.User;
import java.io.*;
import java.util.*;

/**
 * Manages user data operations like loading, saving, deleting, searching, and sorting users.
 */
public class UserManager {

    // The filename for user data
    private final String filename = "users.txt";

    /**
     * Loads users from the file.
     * @return the list of users
     */
    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if(parts.length >= 3) {
                    users.add(new User(parts[0].trim(), parts[1].trim(), parts[2].trim()));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * Saves users to the file.
     * @param users the list of users
     */
    public void saveUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for(User u : users) {
                bw.write(u.getName() + "," + u.getEmail() + "," + u.getPassword());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a user by name.
     * @param name the name of the user to delete
     * @param users the list of users
     */
    public void deleteUser(String name, List<User> users) {
        users.removeIf(u -> u.getName().equalsIgnoreCase(name));
        saveUsers(users);
    }

    /**
     * Deletes all users.
     * @param users the list of users
     */
    public void deleteAllUsers(List<User> users) {
        users.clear();
        saveUsers(users);
    }

    /**
     * Searches users by name keyword.
     * @param keyword the search keyword
     * @param users the list of users
     * @return the matching users
     */
    public List<User> searchUser(String keyword, List<User> users) {
        List<User> result = new ArrayList<>();
        for(User u : users) {
            if(u.getName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(u);
            }
        }
        return result;
    }

    /**
     * Sorts users by name.
     * @param users the list of users
     */
    public void sortUsersByName(List<User> users) {
        Collections.sort(users, (u1, u2) -> u1.getName().compareToIgnoreCase(u2.getName()));
    }
}
