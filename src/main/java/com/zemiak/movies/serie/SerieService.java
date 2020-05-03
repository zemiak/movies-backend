package com.zemiak.movies.serie;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
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
import com.zemiak.movies.movie.Movie;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("series")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class SerieService {
    @GET
    @Path("all")
    public List<Serie> all() {
        return Serie.listAll(Sort.ascending("displayOrder"));
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

        Serie findEntity = Serie.findById(entity.id);
        if (null == findEntity) {
            throw new WebApplicationException(Response.status(Status.GONE).entity("ID not found" + entity.id).build());
        }

        Panache.getEntityManager().merge(entity);
    }

    @GET
    @Path("by-genre/{id}")
    public List<Serie> getGenreSeries(@PathParam("id") @NotNull Long id) {
        return Serie.find("genreId = :genreId",
            Sort.ascending("displayOrder"),
            Parameters.with("genreId", id))
            .list();
    }

    @GET
    @Path("{id}")
    public Serie find(@PathParam("id") @NotNull final Long id) {
        Serie entity = Serie.findById(id);
        if (null == entity) {
            throw new WebApplicationException(Response.status(Status.GONE).entity("ID not found: " + String.valueOf(id)).build());
        }

        return entity;
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull final Long id) {
        Serie entity = Serie.findById(id);
        if (null == entity) {
            throw new WebApplicationException(Response.status(Status.GONE).entity("ID not found: " + String.valueOf(id)).build());
        }

        if (Movie.find("serieId", id).count() > 0) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("They are movies existing with this language." + String.valueOf(id)).build());
        }

        entity.delete();
    }

    public void clearCache(@Observes final CacheClearEvent event) {
        Panache.getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }
}
