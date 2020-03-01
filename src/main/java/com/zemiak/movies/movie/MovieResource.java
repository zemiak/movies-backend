package com.zemiak.movies.movie;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.zemiak.movies.batch.CacheClearEvent;
import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.serie.Serie;
import com.zemiak.movies.strings.Encodings;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class MovieResource {
    @Inject MovieService service;

    @GET
    @Path("all")
    public List<Movie> all() {
        return service.all();
    }

    @GET
    @Path("new")
    public List<Movie> getNewMovies() {
        return Movie.find("genre = :valueNew OR genre IS NULL",
            Sort.ascending("genre", "serie", "displayOrder"),
            Parameters.with("valueNew", Genre.findById(0)))
            .list();
    }

    @GET
    @Path("by-serie/{id}")
    public List<Movie> getSerieMovies(@PathParam("id") @NotNull Long id) {
        return Movie.find("serie = :valueNew OR serie IS NULL",
            Sort.ascending("displayOrder"),
            Parameters.with("valueNew", Serie.findById(id)))
            .list();
    }

    @GET
    @Path("by-genre/{id}")
    public List<Movie> getGenreMovies(@PathParam("id") @NotNull Long id) {
        return Movie.find("genre = :valueNew OR genre IS NULL",
            Sort.ascending("displayOrder"),
            Parameters.with("valueNew", Genre.findById(id)))
            .list();
    }

    @PUT
    public void save(@Valid @NotNull Movie entity) {
        if (null == entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not specified").build());
        }

        entity.persist();
    }

    @POST
    public void create(@Valid @NotNull Movie entity) {
        if (null != entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID specified").build());
        }

        entity.persist();
    }

    @GET
    @Path("{id}")
    public Movie find(@PathParam("id") @NotNull Long id) {
        return service.find(id);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull Long entityId) {
        Movie m = Movie.findById(entityId);
        if (null == m) {
            throw new WebApplicationException(Status.NOT_FOUND);
        }

        m.delete();
    }

    public void clearCache(@Observes CacheClearEvent event) {
        Panache.getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    @GET
    @Path("search/{pattern}")
    public List<Movie> getByExpression(@PathParam("pattern") @NotNull final String text) {
        List<Movie> res = new ArrayList<>();
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
        Movie.streamAll().map(e -> (Movie) e).forEach(entry -> {
            String name = null == entry.name ? ""
                    : Encodings.toAscii(entry.name.trim().toLowerCase());
            if (name.contains(textAscii)) {
                res.add(entry);
            }
        });

        return res;
    }

    @GET
    @Path("last/{count}")
    public List<Movie> getLastMovies(@PathParam("count") @NotNull Integer count) {
        return Movie.findAll(Sort.descending("id")).page(0, count).list();
    }
}
