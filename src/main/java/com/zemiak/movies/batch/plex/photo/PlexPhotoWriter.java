package com.zemiak.movies.batch.plex.photo;

import com.zemiak.movies.batch.service.RefreshStatistics;
import com.zemiak.movies.service.ConfigurationProvider;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class PlexPhotoWriter {
    private static final Logger LOG = Logger.getLogger(PlexPhotoWriter.class.getName());
    public static final String PATH = "Photos";

    @Inject RefreshStatistics stats;

    public void process(final List<String> list) {
        list.stream().filter(obj -> null != obj).forEach(mainYearFolder -> {
            try {
                process(mainYearFolder);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, "Cannot create photo links for year " + mainYearFolder + ": " + ex.getMessage(), ex);
            }
        });
    }

    private void process(String yearFolder) throws IOException {
        String plexLinkPath = ConfigurationProvider.getPlexLinkPath();
        String photoPath = ConfigurationProvider.getPhotoPath();
        Path existing = Paths.get(photoPath, yearFolder);
        Files.createDirectories(Paths.get(plexLinkPath, PATH, yearFolder));

        for (String fileName : existing.toFile().list()) {
            Path albumPath = Paths.get(existing.toString(), fileName);
            final File file = albumPath.toFile();

            if (file.isDirectory() && !fileName.startsWith(".")) {
                Path linkPath = Paths.get(plexLinkPath, PATH, yearFolder, fileName);
                processAlbum(albumPath, linkPath);
            }
        }
    }

    private void processAlbum(Path existing, Path linkPath) throws IOException {
        Files.createDirectories(linkPath);

        for (String fileName : existing.toFile().list()) {
            Path existingPhoto = Paths.get(existing.toString(), fileName);
            final File file = existing.toFile();

            if (file.canRead()) {
                Path linkPhotoPath = Paths.get(linkPath.toString(), fileName);
                Files.createSymbolicLink(linkPhotoPath, existingPhoto);
            }
        }
    }
}
