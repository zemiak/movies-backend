package com.zemiak.movies.folders;

import java.util.ArrayList;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.zemiak.movies.genre.GenreUIService;
import com.zemiak.movies.movie.MovieUIService;
import com.zemiak.movies.serie.SerieUIService;

@RequestScoped
@Transactional
@Path("ui")
public class FolderService {
    @Inject
    GenreUIService genres;

    @Inject
    SerieUIService series;

    @Inject
    MovieUIService movies;

    @GET
    @Path("root")
    public JsonArray getGenres() {
        return genres.getRootItems();
    }

    @GET
    @Path("search")
    public JsonArray getSearchItems(@NotNull @QueryParam("q") final String query) {
        var results = new ArrayList<JsonObject>();

        results.addAll(genres.getByExpression(query));
        results.addAll(series.getByExpression(query));
        results.addAll(movies.getByExpression(query));

        return Json.createArrayBuilder(results).build();
    }
}
