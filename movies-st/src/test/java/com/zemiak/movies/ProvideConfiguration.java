package com.zemiak.movies;

import java.nio.file.Paths;
import java.util.HashMap;

import com.zemiak.movies.config.ConfigurationProvider;

public class ProvideConfiguration {
    public static void init() {
        var conf = new HashMap<String, String>();
        conf.put(ConfigurationProvider.MEDIA_PATH, Paths.get("", "src", "test", "resources", "movies").toAbsolutePath().toString());
        conf.put("system.name", "test");
        conf.put("external.url", "http://127.0.0.1:8081");
        ConfigurationProvider.setProvidedConfig(conf);
    }
}
