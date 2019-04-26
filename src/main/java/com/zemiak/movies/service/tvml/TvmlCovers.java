package com.zemiak.movies.service.tvml;

import com.zemiak.movies.domain.Genre;
import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.domain.Serie;
import com.zemiak.movies.service.ConfigurationProvider;
import com.zemiak.movies.service.GenreService;
import com.zemiak.movies.service.MovieService;
import com.zemiak.movies.service.SerieService;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.inject.Inject;

public class TvmlCovers {
    @Inject MovieService movies;
    @Inject SerieService series;
    @Inject GenreService genres;

    private final String imgPath = ConfigurationProvider.getImgPath();

    public File getCoverFile(String path) {
        String fileName = getFileName(path);

        return new File(fileName);
    }

    public String getFileName(String path) {
        if (path.startsWith("g")) {
            Genre genre = genres.find(getId(path));
            Path existing = Paths.get(imgPath, "genre", genre.getPictureFileName());
            return existing.toString();
        } else if (path.startsWith("s")) {
            Serie serie = series.find(getId(path));
            Path existing = Paths.get(imgPath, "serie", serie.getPictureFileName());
            return existing.toString();
        }

        Movie movie = movies.find(Integer.valueOf(path));
        Path existing = Paths.get(imgPath, "movie", movie.getPictureFileName());
        return existing.toString();
    }

    private Integer getId(String path) {
        return Integer.valueOf(path.substring(1));
    }
}
