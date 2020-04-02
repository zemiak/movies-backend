package com.zemiak.movies.genre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.zemiak.movies.movie.MovieUIService;
import com.zemiak.movies.serie.Serie;
import com.zemiak.movies.serie.SerieService;

@RequestScoped
@Path("genres")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class GenreUIService {
    @Inject
    GenreService genres;

    @Inject
    SerieService series;

    @Inject
    MovieUIService movies;

    public JsonArray getRootItems() {
        List<JsonObject> root = genres.all().stream().map(Genre::toGuiJson).collect(Collectors.toList());
        root.add(Genre.getFreshGenre());
        root.add(Genre.getRecentlyAddedGenre());
        root.add(Genre.getUnassignedGenre());

        return Json.createArrayBuilder(root).build();
    }

    @GET
    @Path("search/{pattern}")
    public List<JsonObject> getByExpression(@PathParam("pattern") @NotNull final String pattern) {
        return Genre.find("UPPER(name) LIKE ?1", "%" + pattern.toUpperCase() + "%")
                .list()
                .stream()
                .map(e -> (Genre) e)
                .map(Genre::toGuiJson)
                .collect(Collectors.toList());
    }

    @GET
    @Path("browse")
    public JsonArray getItemsForUI(@NotNull @QueryParam("id") final Long id) {
        if (Genre.ID_FRESH == id) {
            return getFreshMovies();
        }

        if (Genre.ID_UNASSIGNED == id) {
            return getUnassignedMovies();
        }

        if (Genre.ID_RECENTLY_ADDED == id) {
            return getRecentlyAddedMovies();
        }

        var results = new ArrayList<JsonObject>();
        results.addAll(series.getGenreSeries(id).stream().map(Serie::toGuiJson).collect(Collectors.toList()));
        results.addAll(movies.getGenreMovies(id));

        return Json.createArrayBuilder(results).build();
    }

    private JsonArray getUnassignedMovies() {
        return Json.createArrayBuilder(movies.getUnassignedMovies()).build();
    }

    private JsonArray getRecentlyAddedMovies() {
        return Json.createArrayBuilder(movies.getRecentlyAddedMovies()).build();
    }

    private JsonArray getFreshMovies() {
        return Json.createArrayBuilder(movies.getFreshMovies()).build();
    }
}
