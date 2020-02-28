package com.zemiak.movies.movie;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.serie.Serie;
import com.zemiak.movies.strings.Encodings;

@RequestScoped
@Path("movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class MovieResource {
    @Inject MovieService service;

    @GET
    @Path("all")
    public List<Movie> all() {
        return service.all();
    }

    @GET
    @Path("new")
    public List<Movie> getNewMovies() {
        TypedQuery<Movie> query = em.createQuery("SELECT l FROM Movie l WHERE (l.genre = :genreNew1 OR l.genre IS NULL) ORDER BY l.genre, l.serie, l.displayOrder", Movie.class);
        query.setParameter("genreNew1", em.find(Genre.class, 0));

        return query.getResultList();
    }

    @GET
    @Path("by-serie/{id}")
    public List<Movie> getSerieMovies(@PathParam("id") @NotNull Integer id) {
        Serie serie = em.find(Serie.class, id);
        TypedQuery<Movie> query = em.createQuery("SELECT l FROM Movie l WHERE L.SERIE IS NULL OR l.serie = :serie ORDER BY l.displayOrder", Movie.class);
        query.setParameter("serie", serie);

        return query.getResultList();
    }

    @GET
    @Path("by-genre/{id}")
    public List<Movie> getGenreMovies(@PathParam("id") @NotNull Integer id) {
        Genre genre = em.find(Genre.class, id);
        TypedQuery<Movie> query = em.createQuery("SELECT l FROM Movie l WHERE l.genre IS NULL OR l.genre = :genre ORDER by l.genre, l.serie, l.displayOrder", Movie.class);
        query.setParameter("genre", genre);

        return query.getResultList();
    }

    @PUT
    public void save(@Valid @NotNull Movie entity) {
        Movie target = null;

        if (null == entity.getId()) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not specified").build());
        }

        target = em.find(Movie.class, entity.getId());
        target.copyFrom(entity);
    }

    @POST
    public void create(@Valid @NotNull Movie entity) {
        if (null != entity.getId()) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID specified").build());
        }

        em.persist(entity);
    }

    @GET
    @Path("{id}")
    public Movie find(@PathParam("id") @NotNull Integer id) {
        return service.find(id);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull Integer entityId) {
        em.remove(em.find(Movie.class, entityId));
    }

    public void clearCache(@Observes CacheClearEvent event) {
        em.getEntityManagerFactory().getCache().evictAll();
    }

    @GET
    @Path("search/{pattern}")
    public List<Movie> getByExpression(@PathParam("pattern") @NotNull final String text) {
        List<Movie> res = new ArrayList<>();
        String textAscii = Encodings.toAscii(text.trim().toLowerCase());

        all().stream().forEach(entry -> {
            String name = null == entry.getName() ? ""
                    : Encodings.toAscii(entry.getName().trim().toLowerCase());
            if (name.contains(textAscii)) {
                res.add(entry);
            }
        });

        return res;
    }

    @GET
    @Path("last/{count}")
    public List<Movie> getLastMovies(@PathParam("count") @NotNull Integer count) {
        TypedQuery<Movie> query = em.createQuery("SELECT l FROM Movie l ORDER BY l.id DESC", Movie.class);
        query.setMaxResults(count);

        return query.getResultList();
    }

    @GET
    @Path("new")
    public List<Movie> findAllNew() {
        List<Movie> res = new ArrayList<>();

        all().stream()
                .filter(movie -> null == movie.getGenre() || movie.getGenre().isEmpty())
                .forEach(movie -> res.add(movie));

        return res;
    }
}
