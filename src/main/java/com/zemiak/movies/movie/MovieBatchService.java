package com.zemiak.movies.movie;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class MovieBatchService {
    @Path("filternew")
    @POST
    public List<String> searchByFilename(List<String> data) {
        return data.stream().filter(fileName -> null == Movie.find("fileName", fileName).firstResult()).collect(Collectors.toList());
    }

    @Path("fetch")
    @POST
    public List<MovieUI> getBatchOfMovieData(List<String> data) {
        return data.stream().map(fileName -> Movie.find("fileName", fileName).firstResult()).
            filter(Objects::nonNull).map(m -> MovieUI.of((Movie) m)).collect(Collectors.toList());
    }

    @GET
    @Path("ui/{id}")
    public MovieUI findDetail(@PathParam("id") @NotNull final Long id) {
        Movie entity = Movie.findById(id);
        if (null == entity) {
            throw new WebApplicationException(
                    Response.status(Status.NOT_FOUND).entity("ID not found: " + String.valueOf(id)).build());
        }

        return MovieUI.of(entity);
    }

    @GET
    @Path("ui/paged")
    public List<MovieUI> all(@QueryParam("page") int page, @QueryParam("pageSize") int pageSize) {
        return Movie.findAll(Sort.by("displayOrder")).page(page, pageSize).stream().map(MovieUI::of).collect(Collectors.toList());
    }
}
