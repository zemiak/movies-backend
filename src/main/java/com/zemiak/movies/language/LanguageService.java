package com.zemiak.movies.language;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.validation.ValidationException;
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
@Path("language")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class LanguageService {
    @PersistenceContext
    EntityManager em;

    @GET
    public List<Language> all() {
        TypedQuery<Language> query = em.createQuery("SELECT l FROM Language l ORDER by l.displayOrder", Language.class);

        return query.getResultList();
    }

    @POST
    public void save(Language entity) {
        Language target = null;

        if (null != entity.getId()) {
            target = em.find(Language.class, entity.getId());
        }

        if (null == target) {
            em.persist(entity);
        } else {
            target.copyFrom(entity);
        }
    }

    @GET
    @Path("{id}")
    public Language find(@PathParam("id") String id) {
        return em.find(Language.class, id);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") String entityId) {
        Language bean = em.find(Language.class, entityId);

        if (! bean.getMovieList().isEmpty() || ! bean.getMovieList1().isEmpty() || ! bean.getMovieList2().isEmpty()) {
            throw new ValidationException("They are movies existing with this language.");
        }

        em.remove(bean);
    }

    public void clearCache(@Observes CacheClearEvent event) {
        em.getEntityManagerFactory().getCache().evictAll();
    }

    @GET
    @Path("search/{pattern}")
    public List<Language> getByExpression(@PathParam("pattern") final String text) {
        List<Language> res = new ArrayList<>();

        all().stream().filter(entry -> entry.getName().toLowerCase().contains(text.toLowerCase())).forEach(entry -> {
            res.add(entry);
        });

        return res;
    }
}
