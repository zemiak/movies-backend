package com.zemiak.movies.batch.plex.music;

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
public class PrepareMusicFileList {
    private final List<String> files = new ArrayList<>();

    private static final Logger LOG = Logger.getLogger(PrepareMusicFileList.class.getName());


    @PostConstruct
    public void init() {
        String musicPath = ConfigurationProvider.getMusicPath();

        File mainDir = new File(musicPath);
        if (! mainDir.isDirectory()) {
            LOG.log(Level.SEVERE, "{0} is not a directory", musicPath);
            return;
        }

        if (! mainDir.canExecute() || ! mainDir.canRead()) {
            LOG.log(Level.SEVERE, "{0} is not readable", musicPath);
            return;
        }

        readMusicFiles(mainDir);

        LOG.log(Level.FINE, "Found {0} music files on HDD.", files.size());
    }

    public List<String> getFiles() {
        return files;
    }

    private void readMusicFiles(final File mainDir) {
        for (String fileName : mainDir.list()) {
            final File file = Paths.get(mainDir.getAbsolutePath(), fileName).toFile();

            if ((file.isDirectory()) && (! fileName.startsWith("."))) {
                readMusicFiles(file);
            } else {
                processFile(file.getAbsolutePath());
            }
        }
    }

    private void processFile(final String absolutePath) {
        String ext = getFileExtension(absolutePath);
        String relative = getRelativeFileName(absolutePath);

        if (!relative.startsWith(".") && ("m4a".equals(ext) || "mp3".equals(ext))) {
            files.add(absolutePath);
        }
    }

    public static String getFileExtension(final String absolutePath) {
        final int pos = absolutePath.lastIndexOf(".");
        final String part = absolutePath.substring(pos + 1, absolutePath.length()).toLowerCase();

        return part;
    }

    public static String getRelativeFileName(final String absolutePath) {
        final int pos = absolutePath.lastIndexOf("/");
        final String part = absolutePath.substring(pos + 1, absolutePath.length()).toLowerCase();

        return part;
    }
}
