package com.zemiak.movies.serie;

import java.util.ArrayList;
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
import com.zemiak.movies.strings.Encodings;

import io.quarkus.hibernate.orm.panache.Panache;

@RequestScoped
@Path("series")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class SerieService {
    @GET
    @Path("all")
    public List<Serie> all() {
        return Serie.findAll().list();
    }

    @PUT
    public void save(@Valid @NotNull Serie entity) {
        if (null == entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not specified").build());
        }

        entity.persist();
    }

    @POST
    public void create(@Valid @NotNull Serie entity) {
        if (null != entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID specified").build());
        }

        entity.persist();
    }

    @GET
    @Path("{id}")
    public Serie find(@PathParam("id") @NotNull final Long id) {
        return Serie.findById(id);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull final Long entityId) {
        Serie bean = Serie.findById(entityId);

        if (Movie.find("serie", bean).count() > 0) {
            throw new ValidationException("They are movies existing with this serie.");
        }

        bean.delete();
    }

    public void clearCache(@Observes final CacheClearEvent event) {
        Panache.getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    @GET
    @Path("search/{pattern}")
    public List<Serie> getByExpression(@PathParam("pattern") @NotNull final String text) {
        List<Serie> res = new ArrayList<>();
        String textAscii = Encodings.toAscii(text.trim().toLowerCase());

        Serie.findAll().stream().map(entry -> (Serie) entry).forEach(entry -> {
            String name = (null == entry.name ? ""
                    : Encodings.toAscii(entry.name.trim().toLowerCase()));
            if (name.contains(textAscii)) {
                res.add(entry);
            }
        });

        return res;
    }
}
