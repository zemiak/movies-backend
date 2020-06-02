package com.zemiak.movies.genre;

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
import com.zemiak.movies.serie.Serie;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("genres")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class GenreService {
    @GET
    @Path("all")
    public List<Genre> all() {
        return Genre.listAll(Sort.by("displayOrder"));
    }

    @POST
    public Long create(@Valid @NotNull Genre entity) {
        if (null != entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID specified").build());
        }

        entity.persist();
        return entity.id;
    }

    @PUT
    public void update(@Valid @NotNull Genre entity) {
        if (null == entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID not specified").build());
        }

        Genre findEntity = Genre.findById(entity.id);
        if (null == findEntity) {
            throw new WebApplicationException(Response.status(Status.GONE).entity("ID not found" + entity.id).build());
        }

        Panache.getEntityManager().merge(entity);
    }

    @GET
    @Path("{id}")
    public Genre find(@PathParam("id") @NotNull Long id) {
        Genre entity = Genre.findById(id);
        if (null == entity) {
            throw new WebApplicationException(Response.status(Status.GONE).entity("ID not found: " + String.valueOf(id)).build());
        }

        return entity;
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull Long id) {
        Genre entity = Genre.findById(id);
        if (null == entity) {
            throw new WebApplicationException(Response.status(Status.GONE).entity("ID not found: " + String.valueOf(id)).build());
        }

        if (Serie.find("genreId", entity.id).count() > 0){
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("They are series existing with this genre." + String.valueOf(id)).build());
        }

        if (Movie.find("genreId", entity.id).count() > 0) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("They are movies existing with this genre. ID: " + String.valueOf(id)).build());
        }

        entity.delete();
    }

    public void clearCache(@Observes CacheClearEvent event) {
        Panache.getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }
}
