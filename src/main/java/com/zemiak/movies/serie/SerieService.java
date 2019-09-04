package com.zemiak.movies.serie;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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

@RequestScoped
@Path("series")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class SerieService {
    @PersistenceContext
    EntityManager em;

    @GET
    @Path("all")
    public List<Serie> all() {
        TypedQuery<Serie> query = em.createQuery("SELECT l FROM Serie l ORDER by l.displayOrder", Serie.class);

        return query.getResultList();
    }

    @PUT
    public void save(@Valid @NotNull Serie entity) {
        Serie target = null;

        if (null == entity.getId()) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not specified").build());
        }

        target = em.find(Serie.class, entity.getId());
        target.copyFrom(entity);
    }

    @POST
    public void create(@Valid @NotNull Serie entity) {
        if (null != entity.getId()) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID specified").build());
        }

        em.persist(entity);
    }

    @GET
    @Path("{id}")
    public Serie find(@PathParam("id") @NotNull final Integer id) {
        return em.find(Serie.class, id);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull final Integer entityId) {
        Serie bean = em.find(Serie.class, entityId);

        if (! em.createNamedQuery("Movies.findBySerie", Movie.class).getResultList().isEmpty()) {
            throw new ValidationException("They are movies existing with this serie.");
        }

        em.remove(bean);
    }

    public void clearCache(@Observes final CacheClearEvent event) {
        em.getEntityManagerFactory().getCache().evictAll();
    }

    @GET
    @Path("search/{pattern}")
    public List<Serie> getByExpression(@PathParam("pattern") @NotNull final String text) {
        List<Serie> res = new ArrayList<>();
        String textAscii = Encodings.toAscii(text.trim().toLowerCase());

        all().stream().forEach(entry -> {
            String name = (null == entry.getName() ? ""
                    : Encodings.toAscii(entry.getName().trim().toLowerCase()));
            if (name.contains(textAscii)) {
                res.add(entry);
            }
        });

        return res;
    }
}
