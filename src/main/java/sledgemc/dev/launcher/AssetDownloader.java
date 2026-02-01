/**
 * @author Tinkoprof
 * @summary Utility class for downloading and caching remote assets like agents, APIs, and loaders from GitHub/JitPack.
 */
package sledgemc.dev.launcher;

import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.function.Consumer;

public class AssetDownloader {

    public static final String AGENT_URL = "https://jitpack.io/com/github/Astera-Solutions/SledgeMC-Agent/agent-v1.0.0/SledgeMC-Agent-agent-v1.0.0.jar";
    public static final String API_URL = "https://jitpack.io/com/github/Astera-Solutions/Sledge-API/v1.0.0/Sledge-API-v1.0.0.jar";

    private static String getLoaderUrl(String version) {
        return "https://jitpack.io/com/github/Astera-Solutions/SledgeMC-Loader/" + version + "/SledgeMC-Loader-"
                + version + ".jar";
    }

    private static final Path CACHE_DIR = Paths.get(System.getProperty("user.home"), ".sledgemc", "cache");

    public static Path ensureAgent(Consumer<String> logger) throws Exception {
        return ensureFile(AGENT_URL, "sledge-agent-v1.0.0.jar", logger);
    }

    public static Path ensureApi(Consumer<String> logger) throws Exception {
        return ensureFile(API_URL, "sledge-api-v1.0.0.jar", logger);
    }

    public static Path ensureLoader(String version, Consumer<String> logger) throws Exception {
        return ensureFile(getLoaderUrl(version), "sledge-loader-" + version + ".jar", logger);
    }

    private static Path ensureFile(String url, String fileName, Consumer<String> logger) throws Exception {
        Files.createDirectories(CACHE_DIR);
        Path targetPath = CACHE_DIR.resolve(fileName);

        if (!Files.exists(targetPath)) {
            logger.accept("[SledgeMC] Downloading remote " + fileName + " from GitHub...");
            try (InputStream in = URI.create(url).toURL().openStream()) {
                Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            logger.accept("[SledgeMC] " + fileName + " downloaded successfully to: " + targetPath);
        } else {
            logger.accept("[SledgeMC] Using cached " + fileName + ": " + targetPath);
        }

        return targetPath;
    }
}
