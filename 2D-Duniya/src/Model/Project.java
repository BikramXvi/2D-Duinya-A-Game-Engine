package Model;

/**
 *
 * @author USER
 */
public class Project {
    private String name;
    private String path;

    /**
     *
     * @param name
     * @param path
     */
    public Project(String name, String path) {
        this.name = name;
        this.path = path;
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
    public String getPath() { return path; }

    /**
     *
     * @param name
     */
    public void setName(String name) { this.name = name; }

    /**
     *
     * @param path
     */
    public void setPath(String path) { this.path = path; }
}
