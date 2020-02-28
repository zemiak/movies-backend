package com.zemiak.movies.language;

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

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.panache.common.Parameters;
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

    @PUT
    public void save(@Valid @NotNull Language entity) {
        if (null == entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not specified").build());
        }

        entity.persist();
    }

    @POST
    public void create(@Valid @NotNull Language entity) {
        if (null != entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID specified").build());
        }

        entity.persist();
    }

    @GET
    @Path("{id}")
    public Language find(@PathParam("id") @NotNull Long id) {
        return Language.findById(id);
    }

    @GET
    @Path("{code}/code")
    public Language findByCode(@PathParam("id") @NotNull String code) {
        return Language.find("code", code).firstResult();
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull Long entityId) {
        Language bean = Language.findById(entityId);


        if (Movie.find("language", bean).count() > 0 || Movie.find("originalLanguage", bean).count() > 0 || Movie.find("subtitles", bean).count() > 0) {
            throw new ValidationException("They are movies existing with this language.");
        }

        bean.delete();
    }

    public void clearCache(@Observes CacheClearEvent event) {
        Panache.getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    @GET
    @Path("search/{pattern}")
    public List<Language> getByExpression(@PathParam("pattern") @NotNull final String text) {
        return Language.find("UPPER(name) LIKE UPPER('%:pattern%')", Parameters.with("pattern", text)).list();
    }
}
