package com.zemiak.movies.genre;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.zemiak.movies.movie.MovieUIService;
import com.zemiak.movies.serie.Serie;
import com.zemiak.movies.serie.SerieService;
import com.zemiak.movies.ui.GuiDTO;

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

    public List<GuiDTO> getRootItems() {
        List<GuiDTO> root = genres.all().stream().map(Genre::toDto).collect(Collectors.toList());
        root.add(Genre.getFreshGenre());
        root.add(Genre.getRecentlyAddedGenre());
        root.add(Genre.getUnassignedGenre());

        return root;
    }

    @GET
    @Path("search/{pattern}")
    public List<GuiDTO> getByExpression(@PathParam("pattern") @NotNull final String pattern) {
        return Genre.find("UPPER(name) LIKE ?1", "%" + pattern.toUpperCase() + "%")
                .list()
                .stream()
                .map(e -> (Genre) e)
                .map(Genre::toDto)
                .collect(Collectors.toList());
    }

    @GET
    @Path("thumbnail")
    public Response getThumbnail(@QueryParam("id") final Long id) {
        var e = find(id);
        String fileName = e.pictureFileName;

        if (null == fileName) {
            return Response.status(Status.NOT_FOUND).entity("Thumbnail for " + id + " not yet created").build();
        }

        FileInputStream stream;
        try {
            stream = new FileInputStream(new File(fileName));
        } catch (FileNotFoundException e1) {
            return Response.status(Status.NOT_FOUND).entity("Thumbnail for " + id + " not found " + fileName).build();
        }

        return Response.ok(stream).build();
    }

    protected Genre find(final Long id) {
        return genres.find(id);
    }

    @GET
    @Path("browse")
    public List<GuiDTO> getItemsForUI(@NotNull @QueryParam("id") final Long id) {
        if (Genre.ID_FRESH == id) {
            return getFreshMovies();
        }

        if (Genre.ID_UNASSIGNED == id) {
            return getUnassignedMovies();
        }

        if (Genre.ID_RECENTLY_ADDED == id) {
            return getRecentlyAddedMovies();
        }

        var results = new ArrayList<GuiDTO>();
        results.addAll(series.getGenreSeries(id).stream().map(Serie::toDto).collect(Collectors.toList()));
        results.addAll(movies.getGenreMovies(id));

        return results;
    }

    private List<GuiDTO> getUnassignedMovies() {
        return movies.getUnassignedMovies();
    }

    private List<GuiDTO> getRecentlyAddedMovies() {
        return movies.getRecentlyAddedMovies();
    }

    private List<GuiDTO> getFreshMovies() {
        int year = LocalDateTime.now().get(ChronoField.YEAR);
        return movies.getFreshMovies(year);
    }
}
