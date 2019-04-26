package com.zemiak.movies.service.ui.admin.resource;

import com.zemiak.movies.domain.DataTablesAjaxData;
import com.zemiak.movies.domain.SerieDTO;
import com.zemiak.movies.service.SerieService;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Path("series")
@Produces(value = MediaType.APPLICATION_JSON)
@Consumes(value = MediaType.APPLICATION_JSON)
public class SeriesResource {
    @Inject
    private SerieService series;

    @GET
    public DataTablesAjaxData<SerieDTO> getAllMovies() {
        return new DataTablesAjaxData<>(series.all().stream().map(movie -> new SerieDTO(movie)).collect(Collectors.toList()));
    }
}
