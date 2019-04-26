package com.zemiak.movies.batch.plex;

import com.zemiak.movies.batch.plex.movie.SerieItemWriter;
import com.zemiak.movies.batch.plex.movie.StandaloneMovieWriter;
import com.zemiak.movies.batch.plex.music.PlexMusicWriter;
import com.zemiak.movies.batch.plex.photo.PlexPhotoWriter;
import com.zemiak.movies.batch.service.logs.BatchLogger;
import com.zemiak.movies.service.ConfigurationProvider;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;
import javax.enterprise.context.Dependent;

@Dependent
public class BasicPlexFolderStructureCreator {
    private static final BatchLogger LOG = BatchLogger.getLogger(BasicPlexFolderStructureCreator.class.getName());

    public void cleanAndCreate() {
        String plexLinkPath = ConfigurationProvider.getPlexLinkPath();

        Path directory = Paths.get(plexLinkPath);
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (plexLinkPath.equals(dir.toString())) {
                        return FileVisitResult.CONTINUE;
                    }

                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }

            });
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error deleting plex folder " + plexLinkPath, ex);
        }

        try {
            Files.createDirectories(Paths.get(plexLinkPath, StandaloneMovieWriter.PATH));
            Files.createDirectories(Paths.get(plexLinkPath, SerieItemWriter.PATH));
            Files.createDirectories(Paths.get(plexLinkPath, PlexMusicWriter.PATH));
            Files.createDirectories(Paths.get(plexLinkPath, PlexPhotoWriter.PATH));
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Basic plex folder creation error", ex);
        }
    }
}
