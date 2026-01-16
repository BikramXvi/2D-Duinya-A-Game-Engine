package Main;

import View.Registerframe;

/**
 * The main application entry point for the 2D-Duniya project.
 * Launches the registration frame to start the application.
 * @author Bikram Tamang
 */
public class App {

    /**
     * The main method that starts the application by displaying the registration frame.
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new Registerframe().setVisible(true);
    }
}
