package com.zemiak.movies.genre;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.zemiak.movies.batch.CacheClearEvent;

@RequestScoped
@Path("genre")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GenreService {
    @PersistenceContext EntityManager em;

    @GET
    public List<Genre> all() {
        TypedQuery<Genre> query = em.createQuery("SELECT l FROM Genre l ORDER by l.displayOrder", Genre.class);

        return query.getResultList();
    }

    @POST
    public void save(@Valid @NotNull Genre entity) {
        Genre target = null;

        if (null != entity.getId()) {
            target = em.find(Genre.class, entity.getId());
        }

        if (null == target) {
            em.persist(entity);
        } else {
            target.copyFrom(entity);
        }
    }

    @GET
    @Path("{id}")
    public Genre find(@PathParam("id") Integer id) {
        return em.find(Genre.class, id);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer entityId) {
        Genre bean = em.find(Genre.class, entityId);

        if (! bean.getSerieList().isEmpty()) {
            throw new ValidationException("They are series existing with this genre.");
        }

        if (! bean.getMovieList().isEmpty()) {
            throw new ValidationException("They are movies existing with this genre.");
        }

        em.remove(bean);
    }

    public void clearCache(@Observes CacheClearEvent event) {
        em.getEntityManagerFactory().getCache().evictAll();
    }

    @GET
    @Path("search/{pattern}")
    public List<Genre> getByExpression(@PathParam("pattern") final String text) {
        List<Genre> res = new ArrayList<>();

        all().stream().filter(entry -> entry.getName().toLowerCase().contains(text.toLowerCase())).forEach(entry -> {
            res.add(entry);
        });

        return res;
    }
}
