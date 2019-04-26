package com.zemiak.movies.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Needed ENV keys are listed below.
 *
 * BIN_PATH
 * MEDIA_PATH
 * EXTERNAL_URL
 * SYSTEM_NAME
 * MAIL_TO
 */
public final class ConfigurationProvider {
    private static Map<String, String> providedConfig = null;

    public static void setProvidedConfig(Map<String, String> config) {
        providedConfig = config;
    }

    private static String get(String key) {
        String value = null == providedConfig ? System.getenv(key) : providedConfig.get(key);
        if (null == value || value.trim().isEmpty()) {
            throw new IllegalStateException("Missing configuration " + key);
        }

        return value;
    }

    private static Path getBinPath() {
        return Paths.get(get("BIN_PATH"));
    }

    private static Path getBasePath() {
        return Paths.get(get("MEDIA_PATH"));
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

    public static String getMp4Tags() {
        return Paths.get(getBinPath().toString(), "mp4tags").toString();
    }

    public static String getFFMpegThumbnailer() {
        return Paths.get(getBinPath().toString(), "ffmpegthumbnailer").toString();
    }

    public static String getExternalURL() {
        return get("EXTERNAL_URL");
    }

    public static String getMailTo() {
        return get("MAIL_TO");
    }

    public static String getSystemName() {
        return get("SYSTEM_NAME");
    }

    public static boolean isDevelopmentSystem() {
        return !"prod".equalsIgnoreCase(getSystemName());
    }

    public static String getPlexLinkPath() {
        return Paths.get(getBasePath().toString(), "plex").toString();
    }
}
