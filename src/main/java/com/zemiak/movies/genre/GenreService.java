package com.zemiak.movies.genre;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
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

import com.zemiak.movies.movie.Movie;
import com.zemiak.movies.serie.Serie;

import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("genres")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class GenreService {
    @GET
    @Path("all")
    public List<GenreDTO> all() {
        return Genre.findAll(Sort.ascending("displayOrder")).list().stream().map(GenreDTO::new).collect(Collectors.toList());
    }

    @POST
    public void create(@Valid @NotNull Genre entity) {
        if (null != entity.getId()) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID specified").build());
        }

        entity.persist();
    }

    @PUT
    public void save(@Valid @NotNull Genre entity) {
        Genre target = null;

        if (null == entity.getId()) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not specified").build());
        }

        target = Genre.findById(entity.getId());
        target.copyFrom(entity);
    }

    @GET
    @Path("{id}")
    public GenreDTO find(@PathParam("id") @NotNull Long id) {
        return new GenreDTO(Genre.findById(id));
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull Long entityId) {
        Genre genre = Genre.findById(entityId);

        if (! Serie.findByGenre(genre).isEmpty()) {
            throw new ValidationException("They are series existing with this genre.");
        }

        if (! Movie.findByGenre(genre).isEmpty()) {
            throw new ValidationException("They are movies existing with this genre.");
        }

        genre.delete();
    }

    @GET
    @Path("search/{pattern}")
    public List<GenreDTO> getByExpression(@PathParam("pattern") @NotNull final String text) {
        List<GenreDTO> res = new ArrayList<>();

        all().stream().filter(entry -> entry.getName().toLowerCase().contains(text.toLowerCase())).forEach(entry -> {
            res.add(entry);
        });

        return res;
    }
}
