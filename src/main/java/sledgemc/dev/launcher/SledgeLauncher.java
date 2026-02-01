/**
 * @author Tinkoprof
 * @summary The main JavaFX GUI application for the SledgeMC toolkit, providing an interface for installation and export.
 */
package sledgemc.dev.launcher;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.effect.DropShadow;

import java.io.File;
import java.io.InputStream;

public class SledgeLauncher extends Application {

    private TextField minecraftPathField;
    private TextField modsPathField;
    private TextArea logArea;
    private ComboBox<String> versionCombo;

    private LaunchConfig config;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        config = LaunchConfig.load();

        stage.setTitle("SledgeMC Launcher");
        stage.setResizable(false);

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.TOP_CENTER);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #0a0a0a, #151515);");

        VBox header = new VBox(15);
        header.setAlignment(Pos.CENTER);

        try {
            InputStream logoStream = getClass().getResourceAsStream("/logo.png");
            if (logoStream != null) {
                Image logoImg = new Image(logoStream);
                ImageView logoView = new ImageView(logoImg);
                logoView.setFitHeight(120);
                logoView.setPreserveRatio(true);
                DropShadow glow = new DropShadow(20, Color.web("#4a90e2"));
                logoView.setEffect(glow);
                header.getChildren().add(logoView);
            }
        } catch (Exception e) {
            System.err.println("Could not load logo: " + e.getMessage());
        }

        Label title = new Label("SLEDGE MC");
        title.setFont(Font.font("Inter", FontWeight.EXTRA_BOLD, 32));
        title.setTextFill(Color.WHITE);

        Label subtitle = new Label("ADVANCED MODDING TOOLKIT");
        subtitle.setFont(Font.font("Inter", FontWeight.BOLD, 12));
        subtitle.setTextFill(Color.web("#555555"));

        header.getChildren().addAll(title, subtitle);

        VBox content = new VBox(20);
        content.setPadding(new Insets(10, 0, 10, 0));
        content.setAlignment(Pos.CENTER);

        VBox settingsCard = new VBox(15);
        settingsCard.setPadding(new Insets(20));
        settingsCard.setStyle(
                "-fx-background-color: #0d0d0d; -fx-background-radius: 12; -fx-border-color: #1a1a1a; -fx-border-radius: 12; -fx-border-width: 1;");

        GridPane settings = createSettingsPane();
        settingsCard.getChildren().add(settings);

        VBox logsCard = new VBox(10);
        logsCard.setPadding(new Insets(15));
        logsCard.setStyle(
                "-fx-background-color: #0d0d0d; -fx-background-radius: 12; -fx-border-color: #1a1a1a; -fx-border-radius: 12; -fx-border-width: 1;");

        Label logLabel = new Label("ACTIVITY LOG");
        logLabel.setFont(Font.font("Inter", FontWeight.BOLD, 9));
        logLabel.setTextFill(Color.web("#444444"));

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setPrefHeight(100);
        logArea.setStyle(
                "-fx-control-inner-background: transparent; -fx-background-color: transparent; -fx-text-fill: #777777; -fx-font-family: 'JetBrains Mono', monospace; -fx-font-size: 10px; -fx-border-width: 0;");
        logArea.setFocusTraversable(false);

        logsCard.getChildren().addAll(logLabel, logArea);

        content.getChildren().addAll(settingsCard, logsCard);

        Button installButton = new Button("INSTALL PROFILE");
        installButton.setPrefWidth(240);
        installButton.setPrefHeight(50);
        installButton.setStyle(
                "-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        installButton.setOnAction(e -> installProfile());

        installButton.setOnMouseEntered(e -> installButton.setStyle(
                "-fx-background-color: #4a90e2; -fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));
        installButton.setOnMouseExited(e -> installButton.setStyle(
                "-fx-background-color: #ffffff; -fx-text-fill: #000000; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;"));

        Button exportButton = new Button("EXPORT PRISM BUNDLE");
        exportButton.setPrefWidth(240);
        exportButton.setPrefHeight(50);
        exportButton.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #666666; -fx-font-size: 13px; -fx-font-weight: bold; -fx-border-color: #222222; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        exportButton.setOnAction(e -> exportPrism());

        exportButton.setOnMouseEntered(e -> exportButton.setStyle(
                "-fx-background-color: #111111; -fx-text-fill: #ffffff; -fx-font-size: 13px; -fx-font-weight: bold; -fx-border-color: #333333; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"));
        exportButton.setOnMouseExited(e -> exportButton.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #666666; -fx-font-size: 13px; -fx-font-weight: bold; -fx-border-color: #222222; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;"));

        VBox actions = new VBox(15, installButton, exportButton);
        actions.setAlignment(Pos.CENTER);

        root.getChildren().addAll(header, content, actions);

        Scene scene = new Scene(root, 540, 720);
        stage.setScene(scene);
        stage.show();

        log("SledgeMC Toolkit Initialized");
    }

    private GridPane createSettingsPane() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setAlignment(Pos.CENTER);

        Label mcLabel = new Label("MINECRAFT DATA PATH");
        mcLabel.setFont(Font.font("Inter", FontWeight.BOLD, 10));
        mcLabel.setTextFill(Color.web("#555555"));

        minecraftPathField = new TextField(config.getMinecraftPath());
        minecraftPathField.setPrefWidth(320);
        String fieldStyle = "-fx-background-color: #050505; -fx-text-fill: #eeeeee; -fx-border-color: #222222; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-family: 'Inter';";
        String fieldFocusStyle = "-fx-background-color: #050505; -fx-text-fill: #eeeeee; -fx-border-color: #4a90e2; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 10; -fx-font-family: 'Inter';";
        minecraftPathField.setStyle(fieldStyle);
        minecraftPathField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            minecraftPathField.setStyle(newVal ? fieldFocusStyle : fieldStyle);
        });

        Button mcBrowse = new Button("BROWSE");
        mcBrowse.setPrefHeight(38);
        mcBrowse.setStyle(
                "-fx-background-color: #1a1a1a; -fx-text-fill: #888888; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;");
        mcBrowse.setOnAction(e -> browseDirectory(minecraftPathField, "Select Minecraft Directory"));
        mcBrowse.setOnMouseEntered(e -> mcBrowse.setStyle(
                "-fx-background-color: #252525; -fx-text-fill: #ffffff; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;"));
        mcBrowse.setOnMouseExited(e -> mcBrowse.setStyle(
                "-fx-background-color: #1a1a1a; -fx-text-fill: #888888; -fx-font-size: 10px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-cursor: hand;"));

        grid.add(mcLabel, 0, 0, 2, 1);
        grid.add(minecraftPathField, 0, 1);
        grid.add(mcBrowse, 1, 1);

        Label versionLabel = new Label("MINECRAFT VERSION");
        versionLabel.setFont(Font.font("Inter", FontWeight.BOLD, 10));
        versionLabel.setTextFill(Color.web("#555555"));

        versionCombo = new ComboBox<>();
        versionCombo.getItems().addAll("1.21.4", "1.21.3", "1.21.2", "1.21.1", "1.21");
        versionCombo.setValue("1.21.4");
        versionCombo.setPrefWidth(400);
        versionCombo.setPrefHeight(42);

        String comboStyle = "-fx-background-color: #050505; -fx-mark-color: #4a90e2; -fx-text-fill: white; -fx-border-color: #222222; -fx-border-radius: 6; -fx-background-radius: 6; -fx-padding: 2 10 2 10;";
        versionCombo.setStyle(comboStyle);

        versionCombo.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setTextFill(Color.WHITE);
                    setFont(Font.font("Inter", FontWeight.BOLD, 13));
                }
            }
        });

        versionCombo.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("-fx-background-color: #0a0a0a;");
                } else {
                    setText(item);
                    setTextFill(Color.web("#888888"));
                    setFont(Font.font("Inter", 13));
                    setStyle("-fx-background-color: #0a0a0a; -fx-padding: 10;");

                    setOnMouseEntered(e -> {
                        setStyle("-fx-background-color: #1a1a1a; -fx-padding: 10;");
                        setTextFill(Color.WHITE);
                    });
                    setOnMouseExited(e -> {
                        setStyle("-fx-background-color: #0a0a0a; -fx-padding: 10;");
                        setTextFill(Color.web("#888888"));
                    });
                }
            }
        });

        grid.add(versionLabel, 0, 2, 2, 1);
        grid.add(versionCombo, 0, 3, 2, 1);

        modsPathField = new TextField(config.getModsPath());

        return grid;
    }

    private void browseDirectory(TextField field, String title) {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(title);

        String current = field.getText();
        if (!current.isEmpty()) {
            File dir = new File(current);
            if (dir.exists()) {
                chooser.setInitialDirectory(dir);
            }
        }

        File selected = chooser.showDialog(null);
        if (selected != null) {
            field.setText(selected.getAbsolutePath());
        }
    }

    private void installProfile() {
        String mcPath = minecraftPathField.getText();
        String version = versionCombo.getValue();

        log("> Installing profile for " + version);

        try {
            ProfileInstaller installer = new ProfileInstaller(mcPath, version);
            installer.install();
            log("[SUCCESS] Profile 'SledgeMC-" + version + "' created.");
        } catch (Exception e) {
            log("[ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void exportPrism() {
        String version = versionCombo.getValue();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Prism Instance");
        fileChooser.setInitialFileName("SledgeMC-" + version + ".zip");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip Files", "*.zip"));
        File dest = fileChooser.showSaveDialog(null);

        if (dest != null) {
            log("> Exporting Prism Bundle...");
            try {
                PrismPackager packager = new PrismPackager(version);
                packager.createPackage(dest.toPath());
                log("[SUCCESS] Exported to " + dest.getName());
            } catch (Exception e) {
                log("[ERROR] " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void log(String message) {
        Platform.runLater(() -> {
            logArea.appendText(message + "\n");
        });
    }

    @Override
    public void stop() {
        config.setMinecraftPath(minecraftPathField.getText());
        config.setModsPath(modsPathField.getText());
        config.save();
    }
}
