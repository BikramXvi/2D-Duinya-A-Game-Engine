package Controller;

import Model.Project;
import java.io.*;
import java.util.ArrayList;

/**
 * Manages data for a specific project, handling loading, saving, and modifying project-specific data stored in a file.
 */
public class ProjectDataController {

    // The project this controller manages
    private Project project;
    // The list of data strings for the project
    private ArrayList<String> projectData;

    /**
     * Constructs a ProjectDataController for the given project.
     * @param project the project to manage data for
     */
    public ProjectDataController(Project project) {
        this.project = project;
        this.projectData = new ArrayList<String>();
    }

    /**
     * Gets the data file for this project.
     * @return the data file
     */
    private File getDataFile() {
        return new File(project.getPath(), "project_data.txt");
    }

    /**
     * Loads the project data from the file.
     */
    public void load() {
        projectData.clear();
        File file = getDataFile();
        if (!file.exists()) return;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                projectData.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the project data to the file.
     */
    public void save() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(getDataFile()));
            for (String data : projectData) {
                bw.write(data);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a data string to the project and saves.
     * @param data the data to add
     */
    public void addData(String data) {
        projectData.add(data);
        save();
    }

    /**
     * Gets the list of project data.
     * @return the project data list
     */
    public ArrayList<String> getProjectData() {
        return projectData;
    }

    /**
     * Gets the project.
     * @return the project
     */
    public Project getProject() {
        return project;
    }
}
