/**
 * @author Tinkoprof
 * @summary Main entry point for the JavaFX application, specifically designed to handle fat JAR execution issues.
 */
package sledgemc.dev.launcher;

import javafx.application.Application;

public class Main {
    public static void main(String[] args) {
        Application.launch(SledgeLauncher.class, args);
    }
}
