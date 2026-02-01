/**
 * @author Tinkoprof
 * @summary Handles the installation of SledgeMC into the official Minecraft launcher by merging configurations and deploying libraries.
 */
package sledgemc.dev.launcher;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ProfileInstaller {

    private final String minecraftPath;
    private final String mcVersion;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ProfileInstaller(String minecraftPath, String mcVersion) {
        this.minecraftPath = minecraftPath;
        this.mcVersion = mcVersion;
    }

    public void install() throws IOException {
        String profileName = "SledgeMC-" + mcVersion;
        Path mcDir = Paths.get(minecraftPath);

        Path profileDir = mcDir.resolve("versions").resolve(profileName);
        if (!Files.exists(profileDir)) {
            Files.createDirectories(profileDir);
        }

        Path sourceLoader;
        try {
            sourceLoader = AssetDownloader.ensureLoader(mcVersion, System.out::println);
        } catch (Exception e) {
            throw new IOException("Failed to download loader: " + e.getMessage(), e);
        }

        Path libDir = mcDir.resolve("libraries").resolve("com").resolve("github").resolve("Astera-Solutions")
                .resolve("SledgeMC-Loader").resolve(mcVersion);
        Files.createDirectories(libDir);
        Path targetLib = libDir.resolve("SledgeMC-Loader-" + mcVersion + ".jar");
        Files.copy(sourceLoader, targetLib, StandardCopyOption.REPLACE_EXISTING);

        try {
            Path sourceAgent = AssetDownloader.ensureAgent(System.out::println);
            Path agentLibDir = mcDir.resolve("libraries").resolve("com").resolve("github").resolve("Astera-Solutions")
                    .resolve("SledgeMC-Agent").resolve("agent-v1.0.0");
            Files.createDirectories(agentLibDir);
            Path targetAgentLib = agentLibDir.resolve("SledgeMC-Agent-agent-v1.0.0.jar");
            Files.copy(sourceAgent, targetAgentLib, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.err.println("[SledgeMC] Failed to install agent: " + e.getMessage());
        }

        try {
            Path sourceApi = AssetDownloader.ensureApi(System.out::println);
            Path apiLibDir = mcDir.resolve("libraries").resolve("com").resolve("github").resolve("Astera-Solutions")
                    .resolve("Sledge-API").resolve("v1.0.0");
            Files.createDirectories(apiLibDir);
            Path targetApiLib = apiLibDir.resolve("Sledge-API-v1.0.0.jar");
            Files.copy(sourceApi, targetApiLib, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            System.err.println("[SledgeMC] Failed to install API: " + e.getMessage());
        }

        Path targetVersionJar = profileDir.resolve(profileName + ".jar");
        Files.copy(sourceLoader, targetVersionJar, StandardCopyOption.REPLACE_EXISTING);

        Path vanillaDir = mcDir.resolve("versions").resolve(mcVersion);
        Path vanillaJsonFile = vanillaDir.resolve(mcVersion + ".json");

        if (!Files.exists(vanillaJsonFile)) {
            throw new IOException(
                    "Vanilla version JSON not found: " + vanillaJsonFile + "\nPlease download " + mcVersion
                            + " first.");
        }

        String vanillaContent = Files.readString(vanillaJsonFile);
        JsonObject json = GSON.fromJson(vanillaContent, JsonObject.class);

        json.addProperty("id", profileName);
        json.addProperty("mainClass", "sledgemc.dev.loader.SledgeBootstrap");

        JsonArray libraries = json.getAsJsonArray("libraries");
        if (libraries == null)
            libraries = new JsonArray();

        JsonObject sledgeLib = new JsonObject();
        sledgeLib.addProperty("name", "com.github.Astera-Solutions:SledgeMC-Loader:" + mcVersion);

        JsonObject sledgeAgentLib = new JsonObject();
        sledgeAgentLib.addProperty("name", "com.github.Astera-Solutions:SledgeMC-Agent:agent-v1.0.0");

        JsonObject sledgeApiLib = new JsonObject();
        sledgeApiLib.addProperty("name", "com.github.Astera-Solutions:Sledge-API:v1.0.0");

        JsonArray newLibraries = new JsonArray();
        newLibraries.add(sledgeLib);
        newLibraries.add(sledgeAgentLib);
        newLibraries.add(sledgeApiLib);
        for (JsonElement lib : libraries) {
            newLibraries.add(lib);
        }
        json.add("libraries", newLibraries);

        Path jsonFile = profileDir.resolve(profileName + ".json");
        Files.writeString(jsonFile, GSON.toJson(json));
    }

}
