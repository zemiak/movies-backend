package com.zemiak.movies;

import java.nio.file.Paths;
import java.util.HashMap;

import com.zemiak.movies.config.ConfigurationProvider;

public class ProvideConfiguration {
    public static void init() {
        var conf = new HashMap<String, String>();
        conf.put(ConfigurationProvider.MEDIA_PATH, Paths.get("", "src", "test", "resources", "media").toAbsolutePath().toString());
        conf.put("system.name", "test");
        conf.put("mail.to", "none@npreply");
        ConfigurationProvider.setProvidedConfig(conf);
    }
}
