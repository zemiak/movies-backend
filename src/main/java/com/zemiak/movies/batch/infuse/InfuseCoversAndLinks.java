package com.zemiak.movies.batch.infuse;

import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.domain.Serie;
import com.zemiak.movies.service.ConfigurationProvider;
import com.zemiak.movies.service.MovieService;
import com.zemiak.movies.strings.Encodings;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

@Dependent
public class InfuseCoversAndLinks {
    private static final Logger LOG = Logger.getLogger(InfuseCoversAndLinks.class.getName());

    private final String infuseLinkPath = ConfigurationProvider.getInfuseLinkPath();
    private final String path = ConfigurationProvider.getPath();
    private final String imgPath = ConfigurationProvider.getImgPath();

    @Inject MovieService service;
    @Inject InfuseSerieName serieNamer;
    @Inject InfuseMetadataWriter metadata;

    public void createGenreAndSerieCovers() {
        service.all().stream().forEach(movie -> {
            if (null != movie.getGenre()) {
                createGenreCover(movie);
            }

            if (null != movie.getSerie()) {
                createSerieCover(movie);
            }
        });
    }

    public boolean createLink(Movie movie, String movieName, int order) throws IOException {
        String discriminator = 0 == order ? "" : "_" + order;
        Serie serie = movie.getSerie();
        Path linkName;

        if (null == serie || serie.isEmpty()) {
            Files.createDirectories(Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie))
            ));

            linkName = Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie)),
                    Encodings.deAccent(movieName) + discriminator + ".m4v");
        } else {
            Files.createDirectories(Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie)),
                    Encodings.deAccent(serie.getName())
            ));

            String movieNameInSerie = getSeriedMovieName(movie, movieName, discriminator);
            linkName = Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie)),
                    Encodings.deAccent(serie.getName()),
                    movieNameInSerie);
        }

        Path existing = Paths.get(path, movie.getFileName());

        try {
            Files.createSymbolicLink(linkName, existing);
        } catch (IOException ex) {
            return false;
        }

        createMovieCover(movie, linkName);
        metadata.createMetadataFile(movie, movieName, linkName);

        return true;
    }

    private void createSerieCover(Movie movie) {
        Serie serie = movie.getSerie();
        Path link = Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie)),
                    Encodings.deAccent(serie.getName()),
                    "folder." + getFileExt(serie.getPictureFileName()));
        Path existing = Paths.get(imgPath, "serie", serie.getPictureFileName());

        createSymbolicLink(link, existing);
    }

    private void createGenreCover(Movie movie) {
        Path link = Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie)),
                    "folder." + getFileExt(movie.getGenre().getPictureFileName()));
        Path existing = Paths.get(imgPath, "genre", movie.getGenre().getPictureFileName());

        createSymbolicLink(link, existing);
    }

    private String getFileExt(String name) {
        int pos = name.lastIndexOf(".");
        return name.substring(pos + 1);
    }

    private void createMovieCover(Movie movie, Path linkName) {
        String linkAbsoluteName = linkName.toString();
        int pos = linkAbsoluteName.lastIndexOf("/");
        String fileNameWithExt = linkAbsoluteName.substring(pos + 1);
        String filePath = linkAbsoluteName.substring(0, pos);
        pos = fileNameWithExt.lastIndexOf(".");
        String fileNameWithoutExt = fileNameWithExt.substring(0, pos);
        String ext = getFileExt(fileNameWithExt);

        Path link = Paths.get(filePath, fileNameWithoutExt + "." + getFileExt(movie.getPictureFileName()));
        Path existing = Paths.get(imgPath, "movie", movie.getPictureFileName());
        createSymbolicLink(link, existing);
    }

    private String getGenreName(Movie movie) {
        String name = movie.getGenre().getName();
        if ("Children".equals(name)) {
            name = "0-Children";
        }

        return name;
    }

    private String getSeriedMovieName(Movie movie, String movieName, String discriminator) {
        // Infuse somehow does not group series together :-(

//        if (! movie.getSerie().isTvShow()) {
            return service.getNiceDisplayOrder(movie) + " " + Encodings.deAccent(movieName) + discriminator
                    + ".m4v";
//        }
//
//        return serieNamer.process(movie);
    }

    private void createSymbolicLink(Path link, Path existing) {
        try {
            Files.createSymbolicLink(link, existing);
        } catch (IOException ex) {
            LOG.log(Level.INFO, "Cannot create symbolic link for {0} as {1}", new Object[]{link.toString(), existing.toString()});
        }
    }
}
