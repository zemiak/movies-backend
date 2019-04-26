package com.zemiak.movies.service.ui.play;

import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.service.MovieService;
import com.zemiak.movies.service.ui.admin.JsfMessages;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

@SessionScoped
@Named("movieViewForm")
public class MovieViewForm implements Serializable {
    @Inject private MovieService service;
    @Inject private GenreViewForm genreWebService;
    @Inject private SerieViewForm serieWebService;

    private Integer id;
    private Movie movie;

    public List<Movie> getByGenre() {
        if (-1 == genreWebService.getId() || -2 == genreWebService.getId()) {
            return genreWebService.getBean().getMovieList();
        }

        return service.all().stream()
                .filter(entry -> genreWebService.getId().equals(entry.getGenre().getId()) && entry.isEmptySerie())
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Movie> getBySerie() {
        return service.all().stream()
                .filter(movie -> null != movie.getSerie() && serieWebService.getId().equals(movie.getSerie().getId()))
                .sorted()
                .collect(Collectors.toList());
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public String check() {
        if (null != id) {
            movie = service.find(id);
        }

        if (null == id || null == movie) {
            JsfMessages.addErrorMessage("Movie #" + id + " cannot be found");
            return "index";
        }

        return null;
    }

    public Movie getMovie() {
        return movie;
    }
}
