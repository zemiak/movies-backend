package com.zemiak.movies.language;

import java.util.List;

import javax.enterprise.context.RequestScoped;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

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
    @Path("paged")
    public List<Language> all(@QueryParam("page") int page, @QueryParam("pageSize") int pageSize) {
        return Language.findAll(Sort.by("displayOrder")).page(page, pageSize).list();
    }

    @POST
    public Long create(@Valid @NotNull Language entity) {
        if (Language.find("code", entity.code).singleResultOptional().isPresent()) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("code exists already: " + entity.code).build());
        }

        entity.persist();
        return entity.id;
    }

    @PUT
    public void update(@Valid @NotNull Language entity) {
        if (null == entity.id) {
            throw new WebApplicationException(
                    Response.status(Status.NOT_ACCEPTABLE).entity("ID not specified").build());
        }

        Language findEntity = Language.findById(entity.id);
        if (null == findEntity) {
            throw new WebApplicationException(
                    Response.status(Status.NOT_FOUND).entity("ID not found: " + entity.id).build());
        }

        Panache.getEntityManager().merge(entity);
    }

    @GET
    @Path("{code}")
    public Language find(@PathParam("code") @NotNull String code) {
        Language entity = Language.find("code", code).firstResult();
        if (null == entity) {
            throw new WebApplicationException(
                    Response.status(Status.NOT_FOUND).entity("Code not found: " + String.valueOf(code)).build());
        }

        return entity;
    }

    @DELETE
    @Path("{code}")
    public void remove(@PathParam("code") @NotNull String code) {
        Language entity = Language.find("code", code).firstResult();
        if (null == entity) {
            throw new WebApplicationException(
                    Response.status(Status.NOT_FOUND).entity("Code not found: " + String.valueOf(code)).build());
        }

        if (Movie.find("language", entity.id).count() > 0 || Movie.find("originalLanguage", entity.id).count() > 0
                || Movie.find("subtitlesLanguage", entity.id).count() > 0) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE)
                    .entity("They are movies existing with this language." + String.valueOf(code)).build());
        }

        entity.delete();
    }
}
