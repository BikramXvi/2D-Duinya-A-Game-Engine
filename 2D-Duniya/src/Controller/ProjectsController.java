package Controller;

import Model.Project;
import java.io.*;
import java.util.ArrayList;

/**
 *
 * @author USER
 */
public class ProjectsController {

    private ArrayList<Project> projects = new ArrayList<>();
    private CurrentUserController userController;

    /**
     *
     * @param userController
     */
    public ProjectsController(CurrentUserController userController) {
        this.userController = userController;
    }

    private String getUserFile() {
        String username = userController.getCurrentUser().getUsername();
        return "projects_" + username + ".txt";
    }

    // LOAD projects from file

    /**
     *
     */
    public void loadFromFile() {
        projects.clear();
        File file = new File(getUserFile());
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("\\|");
                projects.add(new Project(data[0], data[1]));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // SAVE projects to file

    /**
     *
     */
    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getUserFile()))) {
            for (Project p : projects) {
                bw.write(p.getName() + "|" + p.getPath());
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // CRUD

    /**
     *
     * @param name
     * @param baseDir
     * @return
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
     *
     * @param index
     * @param newName
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
     *
     * @param index
     */
    public void deleteProject(int index) {
        projects.remove(index);
        saveToFile();
    }

    /**
     *
     * @return
     */
    public ArrayList<Project> getProjects() { return projects; }
}
