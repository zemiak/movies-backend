package com.zemiak.movies.service.ui.admin.resource;

import com.zemiak.movies.domain.DataTablesAjaxData;
import com.zemiak.movies.domain.MovieDTO;
import com.zemiak.movies.service.MovieService;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Path("movies")
@Produces(value = MediaType.APPLICATION_JSON)
@Consumes(value = MediaType.APPLICATION_JSON)
public class MoviesResource {
    @Inject
    private MovieService movies;

    @GET
    @Path("new")
    public DataTablesAjaxData<MovieDTO> getNewMovies() {
        return new DataTablesAjaxData<>(movies.findAllNew().stream().map(movie -> new MovieDTO(movie)).collect(Collectors.toList()));
    }

    @GET
    public DataTablesAjaxData<MovieDTO> getAllMovies() {
        return new DataTablesAjaxData<>(movies.all().stream().map(movie -> new MovieDTO(movie)).collect(Collectors.toList()));
    }
}
