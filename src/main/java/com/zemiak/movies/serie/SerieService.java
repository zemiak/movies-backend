package com.zemiak.movies.serie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
import com.zemiak.movies.genre.GenreRepository;
import com.zemiak.movies.movie.Movie;
import com.zemiak.movies.strings.Encodings;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("series")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class SerieService {
    @Inject
    SerieRepository repo;

    @Inject
    GenreRepository genreRepo;

    @GET
    @Path("all")
    public List<Serie> all() {
        return repo.listAll(Sort.ascending("displayOrder"));
    }

    @POST
    public Long create(@Valid @NotNull Serie entity) {
        if (null != entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID specified").build());
        }

        entity.persist();
        return entity.id;
    }

    @PUT
    public void update(@Valid @NotNull Serie entity) {
        if (null == entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID not specified").build());
        }

        Serie findEntity = repo.findById(entity.id);
        if (null == findEntity) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not found" + entity.id).build());
        }

        Panache.getEntityManager().merge(entity);
    }

    @GET
    @Path("{id}")
    public Serie find(@PathParam("id") @NotNull final Long id) {
        Serie entity = repo.findById(id);
        if (null == entity) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not found: " + String.valueOf(id)).build());
        }

        return entity;
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull final Long id) {
        Serie entity = repo.findById(id);
        if (null == entity) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not found: " + String.valueOf(id)).build());
        }

        if (Movie.find("serieId", entity.id).count() > 0) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("They are movies existing with this language." + String.valueOf(id)).build());
        }

        entity.delete();
    }

    public void clearCache(@Observes final CacheClearEvent event) {
        Panache.getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    @GET
    @Path("search/{pattern}")
    public List<Serie> getByExpression(@PathParam("pattern") @NotNull final String text) {
        List<Serie> res = new ArrayList<>();
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
                res.add(entry);
            }
        });
        stream.close();

        return res;
    }
}
