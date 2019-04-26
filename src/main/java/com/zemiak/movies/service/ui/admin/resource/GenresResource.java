package com.zemiak.movies.service.ui.admin.resource;

import com.zemiak.movies.domain.DataTablesAjaxData;
import com.zemiak.movies.domain.GenreDTO;
import com.zemiak.movies.service.GenreService;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Path("genres")
@Produces(value = MediaType.APPLICATION_JSON)
@Consumes(value = MediaType.APPLICATION_JSON)
public class GenresResource {
    @Inject private GenreService genres;

    @GET
    public DataTablesAjaxData<GenreDTO> getAllMovies() {
        return new DataTablesAjaxData<>(genres.all().stream().map(movie -> new GenreDTO(movie)).collect(Collectors.toList()));
    }
}
