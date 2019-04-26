package com.zemiak.movies.service.ui.play;

import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.domain.Serie;
import com.zemiak.movies.service.MovieService;
import com.zemiak.movies.service.SerieService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named("searchViewForm")
public class SearchViewForm implements Serializable {
    private final List<Serie> series = new ArrayList<>();
    private final List<Movie> movies = new ArrayList<>();

    private String query;

    @Inject private MovieService movieService;
    @Inject private SerieService serieService;

    public void search() {
        if (null != query) {
            searchByQuery(query);
        }
    }

    private void searchByQuery(String query) {
        series.clear();
        serieService.getByExpression(query).stream().forEach((serie) -> {
            series.add(serie);
        });

        movies.clear();
        movieService.getByExpression(query).stream().forEach((movie) -> {
            movies.add(movie);
        });
    }

    public List<Serie> getSeries() {
        return series;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public String getTitle() {
        return null == query ? null : query;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }
}
