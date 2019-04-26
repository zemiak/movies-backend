package com.zemiak.movies.service;

import com.zemiak.movies.domain.CacheClearEvent;
import com.zemiak.movies.domain.Genre;
import com.zemiak.movies.domain.Movie;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;

@Stateless
public class GenreService {
    @PersistenceContext EntityManager em;

    public List<Genre> all() {
        TypedQuery<Genre> query = em.createQuery("SELECT l FROM Genre l ORDER by l.displayOrder", Genre.class);

        return query.getResultList();
    }

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

    public Genre find(Integer id) {
        return em.find(Genre.class, id);
    }

    public void remove(Integer entityId) {
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

    public List<Genre> getByExpression(final String text) {
        List<Genre> res = new ArrayList<>();

        all().stream().filter(entry -> entry.getName().toLowerCase().contains(text.toLowerCase())).forEach(entry -> {
            res.add(entry);
        });

        return res;
    }

    public List<Movie> getMoviesWithoutSerie(Genre genre) {
        Query query = em.createNamedQuery("Movie.findByGenreWithoutSerie");
        query.setParameter("genre", genre);

        return query.getResultList();
    }
}
