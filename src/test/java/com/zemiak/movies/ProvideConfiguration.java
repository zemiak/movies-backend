package com.zemiak.movies;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

import com.zemiak.movies.config.ConfigurationProvider;

public class ProvideConfiguration {
    private static final String MEDIA_PATH = "/tmp";

    public static void init() throws IOException {
        Map<String, String> config = new HashMap<>();
        config.put("MEDIA_PATH", MEDIA_PATH);
        config.put("SYSTEM_NAME", "dev");
        config.put("MAIL_TO", "null@mailinator.com");
        ConfigurationProvider.setProvidedConfig(config);

        deleteFileOrFolder(MEDIA_PATH + "/Movies");
        Files.createDirectories(Paths.get(MEDIA_PATH, "Movies", "new"));
        Files.createDirectories(Paths.get(MEDIA_PATH, "infuse", "None", "Not defined"));
        Files.createDirectories(Paths.get(MEDIA_PATH, "Pictures", "Movies", "movie"));
        Files.createDirectories(Paths.get(MEDIA_PATH, "Pictures", "Movies", "serie"));
        Files.createDirectories(Paths.get(MEDIA_PATH, "Pictures", "Movies", "genre"));
    }

    private static void deleteFileOrFolder(final String path) throws IOException {
        Files.walkFileTree(Paths.get(path), new SimpleFileVisitor<Path>(){
          @Override
          public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
            throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult visitFileFailed(final Path file, final IOException e) {
            return handleException(e);
          }

          private FileVisitResult handleException(final IOException e) {
            e.printStackTrace(); // replace with more robust error handling
            return FileVisitResult.TERMINATE;
          }

          @Override
          public FileVisitResult postVisitDirectory(final Path dir, final IOException e)
            throws IOException {
            if(e!=null)return handleException(e);
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
          }
        });
      };
}
