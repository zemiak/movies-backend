package com.zemiak.movies.infuse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.movie.Movie;
import com.zemiak.movies.movie.MovieService;
import com.zemiak.movies.serie.Serie;
import com.zemiak.movies.strings.Encodings;

import io.quarkus.panache.common.Sort;

@Dependent
public class InfuseCoversAndLinks {
    private static final Logger LOG = Logger.getLogger(InfuseCoversAndLinks.class.getName());

    private final String infuseLinkPath = ConfigurationProvider.getInfuseLinkPath();
    private final String path = ConfigurationProvider.getPath();
    private final String imgPath = ConfigurationProvider.getImgPath();

    @Inject
    MovieService service;

    @Inject
    InfuseSerieName serieNamer;

    @Inject
    InfuseMetadataWriter metadata;

    public void createGenreAndSerieCovers() {
        Movie.traverse(Sort.ascending("id"), movie -> {
            if (null != movie.genreId) {
                createGenreCover(movie);
            }

            if (null != movie.serieId) {
                createSerieCover(movie);
            }
        });
    }

    public boolean createLink(Movie movie, String movieName, int order) throws IOException {
        String discriminator = 0 == order ? "" : "_" + order;
        Serie serie = Serie.findById(movie.serieId);
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
                    Encodings.deAccent(serie.name)
            ));

            String movieNameInSerie = getSeriedMovieName(movie, movieName, discriminator);
            linkName = Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie)),
                    Encodings.deAccent(serie.name),
                    movieNameInSerie);
        }

        if (null == movie.fileName) {
            throw new RuntimeException("fileName of movie " + movie.name + " is null!");
        }

        Path existing = Paths.get(path, movie.fileName);

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
        Serie serie = Serie.findById(movie.serieId);
        Path link = Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie)),
                    Encodings.deAccent(serie.name),
                    "folder." + getFileExt(serie.pictureFileName));
        Path existing = Paths.get(imgPath, "serie", serie.pictureFileName);

        createSymbolicLink(link, existing);
    }

    private void createGenreCover(Movie movie) {
        Path link = Paths.get(infuseLinkPath,
                    Encodings.deAccent(getGenreName(movie)),
                    "folder." + getFileExt(movie.getGenrePictureFileName()));
        Path existing = Paths.get(imgPath, "genre", movie.getGenrePictureFileName());

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

        Path link = Paths.get(filePath, fileNameWithoutExt + "." + getFileExt(movie.pictureFileName));
        Path existing = Paths.get(imgPath, "movie", movie.pictureFileName);
        createSymbolicLink(link, existing);
    }

    private String getGenreName(Movie movie) {
        String name = movie.getGenreName();
        if ("Children".equals(name)) {
            name = "0-Children";
        }

        return name;
    }

    private String getSeriedMovieName(Movie movie, String movieName, String discriminator) {
        // Infuse somehow does not group series together :-(
        return service.getNiceDisplayOrder(movie) + " " + Encodings.deAccent(movieName) + discriminator
                + ".m4v";
    }

    private void createSymbolicLink(Path link, Path existing) {
        try {
            Files.createSymbolicLink(link, existing);
        } catch (IOException ex) {
            LOG.log(Level.INFO, "Cannot create symbolic link for {0} as {1}", new Object[]{link.toString(), existing.toString()});
        }
    }
}
