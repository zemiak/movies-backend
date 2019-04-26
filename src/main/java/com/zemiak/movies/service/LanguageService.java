package com.zemiak.movies.service;

import com.zemiak.movies.domain.CacheClearEvent;
import com.zemiak.movies.domain.Language;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.validation.ValidationException;

@Stateless
public class LanguageService {
    @PersistenceContext
    private EntityManager em;

    public List<Language> all() {
        TypedQuery<Language> query = em.createQuery("SELECT l FROM Language l ORDER by l.displayOrder", Language.class);

        return query.getResultList();
    }

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

    public Language find(String id) {
        return em.find(Language.class, id);
    }

    public void remove(String entityId) {
        Language bean = em.find(Language.class, entityId);

        if (! bean.getMovieList().isEmpty() || ! bean.getMovieList1().isEmpty() || ! bean.getMovieList2().isEmpty()) {
            throw new ValidationException("They are movies existing with this language.");
        }

        em.remove(bean);
    }

    public void clearCache(@Observes CacheClearEvent event) {
        em.getEntityManagerFactory().getCache().evictAll();
    }

    public List<Language> getByExpression(final String text) {
        List<Language> res = new ArrayList<>();

        all().stream().filter(entry -> entry.getName().toLowerCase().contains(text.toLowerCase())).forEach(entry -> {
            res.add(entry);
        });

        return res;
    }
}
