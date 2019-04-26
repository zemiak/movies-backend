package com.zemiak.movies.batch.plex.movie;

import static com.zemiak.movies.batch.plex.music.PrepareMusicFileList.getFileExtension;
import static com.zemiak.movies.batch.plex.music.PrepareMusicFileList.getRelativeFileName;
import com.zemiak.movies.service.ConfigurationProvider;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

@Dependent
public class PrepareMovieFileList {
    //private String path;

    private final List<String> files = new ArrayList<>();
    private static final Logger LOG = Logger.getLogger(PrepareMovieFileList.class.getName());


    @PostConstruct
    public void init() {
        String path = ConfigurationProvider.getPath();

        File mainDir = new File(path);
        if (! mainDir.isDirectory()) {
            LOG.log(Level.SEVERE, " is not a directory" + path);
            return;
        }

        if (! mainDir.canExecute() || ! mainDir.canRead()) {
            LOG.log(Level.SEVERE, "{0} is not readable", path);
            return;
        }

        readMovieFiles(mainDir);
        LOG.log(Level.FINE, "Found {0} movies on HDD.", files.size());
    }

    public List<String> getFiles() {
        return files;
    }

    private void readMovieFiles(final File mainDir) {
        for (String fileName : mainDir.list()) {
            final File file = Paths.get(mainDir.getAbsolutePath(), fileName).toFile();

            if ((file.isDirectory()) && (! fileName.startsWith("."))) {
                readMovieFiles(file);
            } else {
                processFile(file.getAbsolutePath());
            }
        }
    }

    private void processFile(final String absolutePath) {
        String ext = getFileExtension(absolutePath);
        String relative = getRelativeFileName(absolutePath);

        if (!relative.startsWith(".") && ("mp4".equals(ext) || "m4v".equals(ext))) {
            files.add(absolutePath);
        }
    }
}
