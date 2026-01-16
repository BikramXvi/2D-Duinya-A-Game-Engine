package Model;

/**
 * Represents a project with its name, file path, creation date, and deletion date.
 */
public class Project {
    // The name of the project
    private String name;
    // The file path of the project
    private String path;
    // The timestamp when the project was created
    private long dateCreated;
    // The timestamp when the project was deleted
    private long dateDeleted;

    /**
     * Constructs a new Project with the given name and path, setting the creation date to current time.
     * @param name the name of the project
     * @param path the file path of the project
     */
    public Project(String name, String path) {
        this.name = name;
        this.path = path;
        this.dateCreated = System.currentTimeMillis();
    }

    /**
     * Constructs a new Project with the given name, path, and creation date.
     * @param name the name of the project
     * @param path the file path of the project
     * @param dateCreated the timestamp of creation
     */
    public Project(String name, String path, long dateCreated) {
        this.name = name;
        this.path = path;
        this.dateCreated = dateCreated;
    }

    /**
     * Gets the name of the project.
     * @return the project name
     */
    public String getName() { return name; }

    /**
     * Gets the path of the project.
     * @return the project path
     */
    public String getPath() { return path; }

    /**
     * Sets the name of the project.
     * @param name the new name
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets the path of the project.
     * @param path the new path
     */
    public void setPath(String path) { this.path = path; }

    /**
     * Gets the creation date of the project.
     * @return the creation timestamp
     */
    public long getDateCreated() {
        return dateCreated;
    }

    /**
     * Sets the deletion date of the project.
     * @param time the deletion timestamp
     */
    public void setDateDeleted(long time) { this.dateDeleted = time; }

    /**
     * Gets the deletion date of the project.
     * @return the deletion timestamp
     */
    public long getDateDeleted() { return this.dateDeleted; }
}
