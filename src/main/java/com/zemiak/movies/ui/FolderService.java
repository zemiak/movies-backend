package com.zemiak.movies.ui;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.zemiak.movies.genre.GenreUIService;
import com.zemiak.movies.movie.MovieUIService;
import com.zemiak.movies.serie.SerieUIService;

@RequestScoped
@Transactional
@Path("ui")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FolderService {
    @Inject
    GenreUIService genres;

    @Inject
    SerieUIService series;

    @Inject
    MovieUIService movies;

    @GET
    @Path("root")
    public List<GuiDTO> getGenres() {
        return genres.getRootItems();
    }

    @GET
    @Path("search")
    public List<GuiDTO> getSearchItems(@NotNull @QueryParam("q") final String query) {
        var results = new ArrayList<GuiDTO>();

        results.addAll(genres.getByExpression(query));
        results.addAll(series.getByExpression(query));
        results.addAll(movies.getByExpression(query));

        return results;
    }
}
