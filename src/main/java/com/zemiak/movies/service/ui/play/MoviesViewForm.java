package com.zemiak.movies.service.ui.play;

import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.service.MovieService;
import com.zemiak.movies.service.ui.admin.JsfMessages;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named("moviesViewForm")
@SessionScoped
public class MoviesViewForm implements Serializable {
    @Inject private MovieService service;
    private Integer id;
    private List<Movie> movies = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String check() {
        if (null == id || (1 != id && 2 != id)) {
            JsfMessages.addErrorMessage("Type #" + id + " is unknown");
            return "index";
        }

        return null;
    }

    public String getTitle() {
        return Integer.valueOf(1).equals(id) ? "Recently Added" : "New Releases";
    }

    public List<Movie> getByType() {
        return Integer.valueOf(1).equals(id) ? getRecentlyAdded() : getNewReleases();
    }

    private List<Movie> getRecentlyAdded() {
        return service.getRecentlyAdded();
    }

    private List<Movie> getNewReleases() {
        return service.getNewReleases();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void filter() {
        movies = getByType();
    }
}
