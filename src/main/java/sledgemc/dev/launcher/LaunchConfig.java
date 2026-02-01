/**
 * @author Tinkoprof
 * @summary Handles loading, saving, and managing launcher configuration settings in a JSON file.
 */
package sledgemc.dev.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.*;

public class LaunchConfig {

    private static final Path CONFIG_FILE = getConfigPath();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private String minecraftPath;
    private String modsPath;
    private String lastVersion = "1.21.11";
    private int allocatedMemory = 4096;

    public LaunchConfig() {
        this.minecraftPath = getDefaultMinecraftPath();
        this.modsPath = minecraftPath + File.separator + "mods";
    }

    public static LaunchConfig load() {
        try {
            if (Files.exists(CONFIG_FILE)) {
                String json = Files.readString(CONFIG_FILE);
                return GSON.fromJson(json, LaunchConfig.class);
            }
        } catch (Exception e) {
            System.err.println("Failed to load config: " + e.getMessage());
        }
        return new LaunchConfig();
    }

    public void save() {
        try {
            Files.createDirectories(CONFIG_FILE.getParent());
            Files.writeString(CONFIG_FILE, GSON.toJson(this));
        } catch (Exception e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    private static Path getConfigPath() {
        String os = System.getProperty("os.name").toLowerCase();
        Path home = Paths.get(System.getProperty("user.home"));

        if (os.contains("win")) {
            return home.resolve("AppData/Roaming/SledgeMC/config.json");
        } else if (os.contains("mac")) {
            return home.resolve("Library/Application Support/SledgeMC/config.json");
        } else {
            return home.resolve(".config/sledgemc/config.json");
        }
    }

    private static String getDefaultMinecraftPath() {
        String os = System.getProperty("os.name").toLowerCase();
        Path home = Paths.get(System.getProperty("user.home"));

        if (os.contains("win")) {
            return home.resolve("AppData/Roaming/.minecraft").toString();
        } else if (os.contains("mac")) {
            return home.resolve("Library/Application Support/minecraft").toString();
        } else {
            return home.resolve(".minecraft").toString();
        }
    }

    public String getMinecraftPath() {
        return minecraftPath;
    }

    public void setMinecraftPath(String path) {
        this.minecraftPath = path;
    }

    public String getModsPath() {
        return modsPath;
    }

    public void setModsPath(String path) {
        this.modsPath = path;
    }

    public String getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(String version) {
        this.lastVersion = version;
    }

    public int getAllocatedMemory() {
        return allocatedMemory;
    }

    public void setAllocatedMemory(int mb) {
        this.allocatedMemory = mb;
    }
}
