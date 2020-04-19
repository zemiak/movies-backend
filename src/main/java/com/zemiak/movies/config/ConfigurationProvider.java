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
        return Paths.get(get(MEDIA_PATH));
    }

    public static String getPhotoPath() {
        return Paths.get(getBasePath().toString(), "Pictures").toString();
    }

    public static String getInfuseLinkPath() {
        return Paths.get(getBasePath().toString(), "infuse").toString();
    }

    public static String getImgPath() {
        return Paths.get(getBasePath().toString(), "Pictures", "Movies").toString();
    }

    public static String getPath() {
        return Paths.get(getBasePath().toString(), "Movies").toString();
    }

    public static String getMusicPath() {
        return Paths.get(getBasePath().toString(), "Music").toString();
    }

    public static String getExternalURL() {
        return get("external.url");
    }

    public static String getMailTo() {
        return get("mail.to");
    }

    public static String getSystemName() {
        return get("system.name");
    }

    public static boolean isDevelopmentSystem() {
        return !"prod".equalsIgnoreCase(getSystemName());
    }

    public static String getPlexLinkPath() {
        return Paths.get(getBasePath().toString(), "plex").toString();
    }
}
