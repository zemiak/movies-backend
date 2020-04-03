package com.zemiak.movies.serie;

import java.util.ArrayList;
import java.util.List;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.zemiak.movies.movie.MovieUIService;
import com.zemiak.movies.strings.Encodings;
import com.zemiak.movies.ui.GuiDTO;

@RequestScoped
@Path("series")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class SerieUIService {
    @Inject
    MovieUIService movies;

    @GET
    @Path("browse")
    public List<GuiDTO> getItemsForUI(@NotNull @QueryParam("id") final Long id) {
        return movies.getSerieMovies(id);
    }

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
        Stream<Serie> stream = Serie.streamAll();
        stream.map(entry -> (Serie) entry).forEach(entry -> {
            String name = (null == entry.name ? ""
                    : Encodings.toAscii(entry.name.trim().toLowerCase()));
            if (name.contains(textAscii)) {
                res.add(entry.toDto());
            }
        });
        stream.close();

        return res;
    }
}
