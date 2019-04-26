package com.zemiak.movies.service.tvml;

import com.zemiak.movies.domain.Genre;
import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.domain.Serie;
import com.zemiak.movies.service.ConfigurationProvider;
import com.zemiak.movies.service.GenreService;
import com.zemiak.movies.service.MovieService;
import com.zemiak.movies.service.SerieService;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;

public class TvmlReader {
    @Inject GenreService genres;
    @Inject SerieService series;
    @Inject MovieService movies;

    private final String externalUrl = ConfigurationProvider.getExternalURL();

    public TvmlData readData(String path) {
        TvmlData data = new TvmlData();

        if ("/".equals(path)) {
            data.setFolders(genres.all().stream().map(this::genreToFolder).collect(Collectors.toList()));
            data.getFolders().add(getLatestReleasesGenre());
            data.getFolders().add(getRecentlyAddedGenre());
            data.getFolders().add(getMoviesWithoutGenre());
            data.setTitle("Movies");
        } else if (path.startsWith("g")) {
            if ("g:rel".equals(path)) {
                data.setMovies(readLatestReleases());
                data.setTitle("Recently Released");
            } else if ("g:add".equals(path)) {
                data.setMovies(readRecentlyAdded());
                data.setTitle("Recently Added");
            } else if ("g:none".equals(path)) {
                data.setMovies(readMoviesWithoutGenre());
                data.setTitle("No Genre");
            } else {
                data.setFolders(readGenreSeries(getId(path)));
                data.setMovies(readGenreMovies(getId(path)));
                data.setTitle(getGenreTitle(getId(path)));
            }
        } else if (path.startsWith("s")) {
            data.setMovies(readSerieMovies(getId(path)));
            data.setTitle(getSerieTitle(getId(path)));
        }

        return data;
    }

    private Integer getId(String path) {
        return Integer.valueOf(path.substring(1));
    }

    private FolderData genreToFolder(Genre genre) {
        FolderData data = new FolderData();
        data.setName(genre.getName());
        data.setPath("g" + genre.getId());

        return data;
    }

    private List<FolderData> readGenreSeries(Integer id) {
        return series
                .all()
                .stream()
                .filter(s -> s.getGenre().getId().equals(id))
                .map(this::serieToFolder)
                .collect(Collectors.toList());
    }

    private FolderData serieToFolder(Serie serie) {
        FolderData data = new FolderData();
        data.setName(serie.getName());
        data.setPath("s" + serie.getId());
        data.setDisplayOrder(serie.getDisplayOrder());

        return data;
    }

    private List<MovieData> readGenreMovies(Integer id) {
        Genre genre = genres.find(id);
        return movies.getGenreMovies(genre).stream().filter(m -> null == m.getSerie() || m.getSerie().isEmpty()).map(this::movieToData).collect(Collectors.toList());
    }

    private List<MovieData> readSerieMovies(Integer id) {
        Serie serie = series.find(id);
        return movies.getOnlySerieMovies(serie).stream().map(this::movieToData).collect(Collectors.toList());
    }

    private MovieData movieToData(Movie movie) {
        MovieData data = new MovieData();
        data.setDescription(movie.getDescription());
        data.setGenreName(movie.getGenreName());
        data.setName(movie.getName());
        data.setPath(externalUrl + "stream/" + movie.getId());
        data.setSerieName(movie.getSerieName());
        data.setYear(null == movie.getYear() ? "" : movie.getYear().toString());
        data.setDisplayOrder(movie.getDisplayOrder());
        data.setId(movie.getId());
        data.setGenreKey(null == movie.getGenre() ? "" : "g" + movie.getGenre().getId());
        data.setSerieKey(null == movie.getSerie() ? "" : "s" + movie.getSerie().getId());

        return data;
    }

    private List<MovieData> readLatestReleases() {
        return movies.getNewReleases().stream().map(this::movieToData).collect(Collectors.toList());
    }

    private List<MovieData> readRecentlyAdded() {
        return movies.getRecentlyAdded().stream().map(this::movieToData).collect(Collectors.toList());
    }

    private FolderData getLatestReleasesGenre() {
        FolderData data = new FolderData();
        data.setName("Latest Releases");
        data.setPath("g:rel");

        return data;
    }

    private FolderData getRecentlyAddedGenre() {
        FolderData data = new FolderData();
        data.setName("Recently Added");
        data.setPath("g:add");

        return data;
    }

    private FolderData getMoviesWithoutGenre() {
        FolderData data = new FolderData();
        data.setName("New Imports");
        data.setPath("g:none");

        return data;
    }

    private List<MovieData> readMoviesWithoutGenre() {
        return movies.getNewMovies().stream().map(this::movieToData).collect(Collectors.toList());
    }

    private String getGenreTitle(Integer id) {
        return genres.find(id).getName();
    }

    private String getSerieTitle(Integer id) {
        return series.find(id).getName();
    }
}
