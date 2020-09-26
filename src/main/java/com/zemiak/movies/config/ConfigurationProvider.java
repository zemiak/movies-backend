package com.zemiak.movies.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.eclipse.microprofile.config.ConfigProvider;

/**
 * Needed ENV keys are listed below.
 *
 * MEDIA_PATH
 * EXTERNAL_URL
 * SYSTEM_NAME
 * MAIL_TO
 */
public final class ConfigurationProvider {
    public static final String MEDIA_PATH = "media.path";
    private static Map<String, String> providedConfig = null;

    public static void setProvidedConfig(Map<String, String> config) {
        providedConfig = config;
    }

    private static String get(String key) {
        String value = null == providedConfig ? ConfigProvider.getConfig().getValue(key, String.class) : providedConfig.get(key);
        if (null == value || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing configuration " + key);
        }

        return value;
    }

    private static Path getBasePath() {
        var path = get(MEDIA_PATH);
        if (! path.startsWith("*")) {
            return Paths.get(path);
        }

        var cwdWithTarget = System.getProperty("user.dir");
        var pos = cwdWithTarget.lastIndexOf("/");
        var cwdWithoutTarget = cwdWithTarget.substring(0, pos);
        var devMediaPath = Paths.get(cwdWithoutTarget, path.substring(1));
        return devMediaPath;
    }

    public static String getInfuseLinkPath() {
        return Paths.get(getBasePath().toString(), "infuse", "Metadata").toString();
    }

    public static String getImgPath() {
        return Paths.get(getBasePath().toString(), "infuse", "Pictures").toString();
    }

    public static String getPath() {
        return Paths.get(getBasePath().toString(), "Movies").toString();
    }

    public static String getExternalURL() {
        return get("external.url");
    }
}
