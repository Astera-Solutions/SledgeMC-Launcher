/**
 * @author Tinkoprof
 * @summary Manages the preparation and execution of the Minecraft process with the SledgeMC agent and custom classpath.
 */
package sledgemc.dev.launcher;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.Consumer;

public class MinecraftLauncher {

    private final String minecraftPath;
    private final String modsPath;
    private final String version;
    private Consumer<String> logger = System.out::println;

    public MinecraftLauncher(String minecraftPath, String modsPath, String version) {
        this.minecraftPath = minecraftPath;
        this.modsPath = modsPath;
        this.version = version;
    }

    public void setLogger(Consumer<String> logger) {
        this.logger = logger;
    }

    public void launch() throws Exception {
        log("Preparing launch...");

        Path mcDir = Paths.get(minecraftPath);
        Path versionsDir = mcDir.resolve("versions").resolve(version);
        Path versionJar = versionsDir.resolve(version + ".jar");
        Path agentJar = AssetDownloader.ensureAgent(this.logger);

        if (!Files.exists(mcDir)) {
            throw new FileNotFoundException("Minecraft directory not found: " + mcDir);
        }

        if (!Files.exists(versionJar)) {
            throw new FileNotFoundException(
                    "Version JAR not found: " + versionJar + ". Please run vanilla Minecraft " + version + " first.");
        }

        Path modsPathDir = Paths.get(modsPath);
        if (!Files.exists(modsPathDir)) {
            Files.createDirectories(modsPathDir);
            log("Created mods directory: " + modsPathDir);
        }

        log("Version JAR: " + versionJar);
        log("Agent JAR: " + agentJar);

        String classpath = buildClasspath(mcDir, versionsDir, versionJar);

        List<String> command = new ArrayList<>();
        command.add(getJavaPath());

        command.add("-Xmx4G");
        command.add("-Xms1G");
        command.add("-XX:+UnlockExperimentalVMOptions");
        command.add("-XX:+UseG1GC");

        Path loaderJar = AssetDownloader.ensureLoader(this.version, this.logger);
        classpath = loaderJar.toAbsolutePath().toString() + File.pathSeparator + classpath;

        command.add("-Djava.library.path=" + versionsDir.resolve("natives"));
        command.add("-Dminecraft.launcher.brand=SledgeMC");
        command.add("-Dminecraft.launcher.version=1.0.0");

        command.add("-cp");
        command.add(classpath);
        command.add("sledgemc.dev.loader.SledgeBootstrap");

        command.add("--version");
        command.add(version);
        command.add("--gameDir");
        command.add(minecraftPath);
        command.add("--assetsDir");
        command.add(mcDir.resolve("assets").toString());

        log("Starting Minecraft...");
        log("Full Command: " + String.join(" ", command));

        ProcessBuilder pb = new ProcessBuilder(command);
        pb.directory(mcDir.toFile());
        pb.redirectErrorStream(true);

        Process process = pb.start();

        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log(line);
                }
            } catch (IOException e) {
                log("Error reading output: " + e.getMessage());
            }
        }, "MC-Output").start();

        log("Minecraft launched with PID: " + process.pid());
    }

    private String buildClasspath(Path mcDir, Path versionsDir, Path versionJar) throws IOException {
        Set<String> entries = new LinkedHashSet<>();

        entries.add(versionJar.toAbsolutePath().toString());

        Path versionJson = versionsDir.resolve(version + ".json");
        if (Files.exists(versionJson)) {
            log("Found version JSON, parsing libraries...");
        }

        Path librariesDir = mcDir.resolve("libraries");
        if (Files.exists(librariesDir)) {
            log("Scanning libraries: " + librariesDir);
            Files.walk(librariesDir)
                    .filter(p -> p.toString().endsWith(".jar"))
                    .forEach(p -> entries.add(p.toAbsolutePath().toString()));
        }

        return String.join(File.pathSeparator, entries);
    }

    private String getJavaPath() {
        String javaHome = System.getProperty("java.home");
        String os = System.getProperty("os.name").toLowerCase();
        String ext = os.contains("win") ? ".exe" : "";
        return Paths.get(javaHome, "bin", "java" + ext).toString();
    }

    private void log(String msg) {
        logger.accept(msg);
    }
}
