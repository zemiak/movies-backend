package com.zemiak.movies.language;

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
import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("languages")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class LanguageService {
    @GET
    @Path("all")
    public List<Language> all() {
        return Language.listAll(Sort.by("displayOrder"));
    }

    @POST
    public Long create(@Valid @NotNull Language entity) {
        if (null != entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID specified").build());
        }

        entity.persist();
        return entity.id;
    }

    @PUT
    public void update(@Valid @NotNull Language entity) {
        if (null == entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID not specified").build());
        }

        Language findEntity = Language.findById(entity.id);
        if (null == findEntity) {
            throw new WebApplicationException(Response.status(Status.GONE).entity("ID not found" + entity.id).build());
        }

        Panache.getEntityManager().merge(entity);
    }

    @GET
    @Path("{id}")
    public Language find(@PathParam("id") @NotNull Long id) {
        Language entity = Language.findById(id);
        if (null == entity) {
            throw new WebApplicationException(Response.status(Status.GONE).entity("ID not found: " + String.valueOf(id)).build());
        }

        return entity;
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull Long id) {
        Language entity = Language.findById(id);
        if (null == entity) {
            throw new WebApplicationException(Response.status(Status.GONE).entity("ID not found: " + String.valueOf(id)).build());
        }

        if (Movie.find("languageId", entity.id).count() > 0 || Movie.find("originalLanguageId", entity.id).count() > 0 || Movie.find("subtitlesId", entity.id).count() > 0) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("They are movies existing with this language." + String.valueOf(id)).build());
        }

        entity.delete();
    }

    @GET
    @Path("{code}/code")
    public Language findByCode(@PathParam("code") @NotNull String code) {
        Language entity = Language.find("code", code).firstResult();
        if (null == entity) {
            throw new WebApplicationException(Response.status(Status.GONE).entity("Code not found: " + String.valueOf(code)).build());
        }

        return entity;
    }

    public void clearCache(@Observes CacheClearEvent event) {
        Panache.getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    @GET
    @Path("search/{pattern}")
    public List<Language> getByExpression(@PathParam("pattern") @NotNull final String pattern) {
        return Language.find("UPPER(name) LIKE ?1", "%" + pattern.toUpperCase() + "%").list();
    }
}
