package com.zemiak.movies.genre;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
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
import io.quarkus.panache.common.Parameters;
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
    public void create(@Valid @NotNull Genre entity) {
        if (null != entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID specified").build());
        }

        entity.persist();
    }

    @PUT
    public void update(@Valid @NotNull Genre entity) {
        if (null == entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID not specified").build());
        }

        entity.persist();
    }

    @GET
    @Path("{id}")
    public Genre find(@PathParam("id") @NotNull Long id) {
        return Genre.findById(id);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull Long entityId) {
        Genre bean = Genre.findById(entityId);

        if (Serie.find("genre", bean).count() > 0){
            throw new ValidationException("They are series existing with this genre.");
        }

        if (Movie.find("genre", bean).count() > 0) {
            throw new ValidationException("They are movies existing with this genre.");
        }

        bean.delete();
    }

    public void clearCache(@Observes CacheClearEvent event) {
        Panache.getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    @GET
    @Path("search/{pattern}")
    public List<Genre> getByExpression(@PathParam("pattern") @NotNull final String text) {
        return Genre.find("UPPER(name) LIKE UPPER('%:pattern%')", Parameters.with("pattern", text)).list();
    }
}
