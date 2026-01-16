package Controller;

import Model.Project;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Manages projects, favourites, and trash for the current user, handling loading, saving, and CRUD operations.
 */
public class ProjectsController {

    // List of all projects
    private ArrayList<Project> projects = new ArrayList<>();
    // List of favourite projects
    private ArrayList<Project> favourites = new ArrayList<>();
    // List of deleted projects in trash
    private ArrayList<TrashItem> trash = new ArrayList<>();
    // Controller for the current user
    private CurrentUserController userController;

    /**
     * Represents a project in the trash with its deletion timestamp.
     */
    public static class TrashItem {
        // The deleted project
        public Project project;
        // Timestamp of deletion
        public long deletedTime;

        /**
         * Constructs a TrashItem for the given project with current time.
         * @param project the project
         */
        public TrashItem(Project project) {
            this.project = project;
            this.deletedTime = System.currentTimeMillis();
        }
    }

    /**
     * Constructs a ProjectsController for the given user controller.
     * @param userController the current user controller
     */
    public ProjectsController(CurrentUserController userController) {
        this.userController = userController;
    }

    /**
     * Gets the filename for the user's projects file.
     * @return the filename
     */
    private String getUserFile() {
        return "projects_" + userController.getCurrentUser().getUsername() + ".txt";
    }

    /**
     * Gets the filename for the user's favourites file.
     * @return the filename
     */
    private String getFavouritesFile() {
        return "favourites_" + userController.getCurrentUser().getUsername() + ".txt";
    }

    /**
     * Gets the filename for the user's trash file.
     * @return the filename
     */
    private String getTrashFile() {
        return "trash_" + userController.getCurrentUser().getUsername() + ".txt";
    }

    /**
     * Loads projects from the user's file.
     */
    public void loadFromFile() {
        projects.clear();
        File file = new File(getUserFile());
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data.length >= 3) {
                    try {
                        projects.add(new Project(
                                data[0].trim(),
                                data[1].trim(),
                                Long.parseLong(data[2].trim())
                        ));
                    } catch (NumberFormatException ex) {
                        System.out.println("Skipping line with invalid number: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves projects to the user's file.
     */
    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getUserFile()))) {
            for (Project p : projects) {
                bw.write(p.getName() + "|" + p.getPath() + "|" + p.getDateCreated());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads favourites from the user's file.
     */
    public void loadFavourites() {
        favourites.clear();
        File file = new File(getFavouritesFile());
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data.length >= 3) {
                    try {
                        favourites.add(new Project(
                                data[0].trim(),
                                data[1].trim(),
                                Long.parseLong(data[2].trim())
                        ));
                    } catch (NumberFormatException ex) {
                        System.out.println("Skipping invalid line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves favourites to the user's file.
     */
    public void saveFavourites() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getFavouritesFile()))) {
            for (Project p : favourites) {
                bw.write(p.getName() + "|" + p.getPath() + "|" + p.getDateCreated());
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads trash from the user's file.
     */
    public void loadTrash() {
        trash.clear();
        File file = new File(getTrashFile());
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                if (data.length >= 4) { // name|path|dateCreated|deletedTime
                    try {
                        Project p = new Project(
                                data[0].trim(),
                                data[1].trim(),
                                Long.parseLong(data[2].trim())
                        );
                        long deletedTime = Long.parseLong(data[3].trim());
                        trash.add(new TrashItem(p) {{ deletedTime = deletedTime; }});
                    } catch (NumberFormatException ex) {
                        System.out.println("Skipping invalid line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves trash to the user's file.
     */
    public void saveTrash() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getTrashFile()))) {
            for (TrashItem t : trash) {
                bw.write(t.project.getName() + "|" + t.project.getPath() + "|" + t.project.getDateCreated() + "|" + t.deletedTime);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new project with the given name in the base directory.
     * @param name the project name
     * @param baseDir the base directory
     * @return the created project
     */
    public Project createProject(String name, File baseDir) {
        File dir = new File(baseDir, name);
        dir.mkdir();
        Project p = new Project(name, dir.getAbsolutePath());
        projects.add(p);
        saveToFile();
        return p;
    }

    /**
     * Renames the project at the given index.
     * @param index the index of the project
     * @param newName the new name
     */
    public void renameProject(int index, String newName) {
        Project p = projects.get(index);
        File oldDir = new File(p.getPath());
        File newDir = new File(oldDir.getParent(), newName);

        if (oldDir.renameTo(newDir)) {
            p.setName(newName);
            p.setPath(newDir.getAbsolutePath());
            saveToFile();
        }
    }

    /**
     * Deletes the project at the given index and moves it to trash.
     * @param index the index of the project
     */
    public void deleteProject(int index) {
        Project p = projects.remove(index);
        addToTrash(p);
        saveToFile();
        saveTrash();
    }

    /**
     * Adds a project to favourites.
     * @param p the project to add
     */
    public void addToFavourites(Project p) {
        if (!favourites.contains(p)) {
            favourites.add(p);
            saveFavourites();
        }
    }

    /**
     * Removes a project from favourites.
     * @param p the project to remove
     */
    public void removeFromFavourites(Project p) {
        favourites.remove(p);
        saveFavourites();
    }

    /**
     * Gets the list of favourite projects.
     * @return the favourites list
     */
    public ArrayList<Project> getFavourites() {
        return favourites;
    }

    /**
     * Adds a project to trash.
     * @param p the project to add
     */
    public void addToTrash(Project p) {
        trash.add(new TrashItem(p));
        saveTrash();
    }

    /**
     * Removes a project from trash.
     * @param p the project to remove
     */
    public void removeFromTrash(Project p) {
        trash.removeIf(t -> t.project.equals(p));
        saveTrash();
    }

    /**
     * Gets the list of trash items.
     * @return the trash list
     */
    public ArrayList<TrashItem> getTrash() {
        return trash;
    }

    /**
     * Sorts projects by name.
     */
    public void sortByName() {
        Collections.sort(projects, Comparator.comparing(Project::getName, String.CASE_INSENSITIVE_ORDER));
    }

    /**
     * Sorts projects by creation date.
     */
    public void sortByDateCreated() {
        Collections.sort(projects, Comparator.comparingLong(Project::getDateCreated));
    }

    /**
     * Gets the list of all projects.
     * @return the projects list
     */
    public ArrayList<Project> getProjects() {
        return projects;
    }
}
