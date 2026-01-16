
package Model;
import java.time.LocalDateTime;

/**
 * Represents an item within a project, with name, creation date, and category.
 */
public class ProjectItem {

    // The name of the project item
    private String name;
    // The creation date and time
    private LocalDateTime dateCreated;
    // The category of the item
    private Category category;

    /**
     * Constructs a ProjectItem with the given name, date, and category.
     * @param name the name
     * @param dateCreated the creation date
     * @param category the category
     */
    public ProjectItem(String name, LocalDateTime dateCreated, Category category) {
        this.name = name;
        this.dateCreated = dateCreated;
        this.category = category;
    }

    /**
     * Gets the name.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the creation date.
     * @return the date
     */
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    /**
     * Gets the category.
     * @return the category
     */
    public Category getCategory() {
        return category;
    }
}
