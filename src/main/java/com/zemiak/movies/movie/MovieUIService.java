package com.zemiak.movies.movie;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.strings.Encodings;
import com.zemiak.movies.ui.GuiDTO;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class MovieUIService {
    @Inject
    MovieService movies;

    @GET
    @Path("search/{pattern}")
    public List<GuiDTO> getByExpression(@PathParam("pattern") @NotNull final String text) {
        List<GuiDTO> res = new ArrayList<>();
        String textAscii = Encodings.toAscii(text.trim().toLowerCase());

        /**
         * TODO: optimize findAll() - either set page and size or do the filtering with SQL
         *
         * https://quarkus.io/guides/hibernate-orm-panache
         *
         * "You should only use list and stream methods if your table contains small enough data sets.
         * For larger data sets you can use the find method equivalents, which return a PanacheQuery
         * on which you can do paging"
         */
        Stream<Movie> stream = Movie.streamAll();
        stream.map(e -> (Movie) e).forEach(entry -> {
            String name = null == entry.name ? ""
                    : Encodings.toAscii(entry.name.trim().toLowerCase());
            if (name.contains(textAscii)) {
                res.add(entry.toDto());
            }
        });
        stream.close();

        return res;
    }

    public List<GuiDTO> getRecentlyAddedMovies() {
        return Movie.findAll(Sort.descending("id")).page(0, 50).list().stream().map(e -> (Movie) e).map(Movie::toDto).collect(Collectors.toList());
    }

    public List<GuiDTO> getFreshMovies() {
        int year = LocalDateTime.now().get(ChronoField.YEAR);
        return movies.getNewReleases(year).stream().map(Movie::toDto).collect(Collectors.toList());
    }

    public List<GuiDTO> getSerieMovies(final Long id) {
        return Movie.find("serieId = :serieId",
            Sort.ascending("displayOrder"),
            Parameters.with("serieId", id))
            .list().stream().map(e -> (Movie) e).map(Movie::toDto).collect(Collectors.toList());
    }

    public List<GuiDTO> getGenreMovies(final Long id) {
        return Movie.find("genreId = :genreId",
            Sort.ascending("displayOrder"),
            Parameters.with("genreId", id))
            .list().stream().map(e -> (Movie) e).map(Movie::toDto).collect(Collectors.toList());
    }

    public List<GuiDTO> getUnassignedMovies() {
        return Movie.find("genreId = :genreId OR genreId IS NULL",
            Sort.ascending("displayOrder"),
            Parameters.with("genreId", Genre.ID_NONE))
            .list().stream().map(e -> (Movie) e).map(Movie::toDto).collect(Collectors.toList());
    }
}
