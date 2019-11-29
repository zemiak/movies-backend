package com.zemiak.movies.movie;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
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

import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.serie.Serie;
import com.zemiak.movies.strings.Encodings;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

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
        return Movie.find("genre = ?1 OR genre IS NULL", Sort.ascending("genre", "serie", "displayOrder"), Genre.findById(0)).list();
    }

    @GET
    @Path("by-serie/{id}")
    public List<Movie> getSerieMovies(@PathParam("id") @NotNull Integer id) {
        return Movie.find("serie = ?1 OR serie IS NULL", Sort.ascending("displayOrder"), Serie.findById(id)).list();
    }

    @GET
    @Path("by-genre/{id}")
    public List<Movie> getGenreMovies(@PathParam("id") @NotNull Integer id) {
        return Movie.find("genre = ?1 OR genre IS NULL", Sort.ascending("genre", "serie", "displayOrder"), Genre.findById(id)).list();
    }

    @PUT
    public void save(@Valid @NotNull Movie entity) {
        if (null == entity.getId()) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not specified").build());
        }

        Movie target = Movie.findById(entity.getId());
        target.copyFrom(entity);
        target.persist();
    }

    @POST
    public void create(@Valid @NotNull Movie entity) {
        if (null != entity.getId()) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID specified").build());
        }

        entity.persist();
    }

    @GET
    @Path("{id}")
    public Movie find(@PathParam("id") @NotNull Integer id) {
        return service.find(id);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull Integer entityId) {
        Movie target = Movie.findById(entityId);
        if (null == target) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not found" + entityId).build());
        }

        target.delete();
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
        PanacheQuery<Movie> query = Movie.findAll(Sort.descending("id"));
        query.page(Page.ofSize(count));
        return query.lastPage().list();
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
