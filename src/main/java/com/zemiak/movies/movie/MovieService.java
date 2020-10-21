package com.zemiak.movies.movie;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.RequestScoped;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class MovieService {
    @GET
    @Path("paged")
    public List<Movie> all(@QueryParam("page") int page, @QueryParam("pageSize") int pageSize) {
        return Movie.findAll(Sort.by("displayOrder")).page(page, pageSize).list();
    }

    @POST
    public Long create(@Valid @NotNull Movie entity) {
        if (null != entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID specified").build());
        }

        // entity.id = seq.getNextValue();
        entity.persist();
        return entity.id;
    }

    @PUT
    public void update(@Valid @NotNull Movie entity) {
        if (null == entity.id) {
            throw new WebApplicationException(
                    Response.status(Status.NOT_ACCEPTABLE).entity("ID not specified").build());
        }

        Movie findEntity = Movie.findById(entity.id);
        if (null == findEntity) {
            throw new WebApplicationException(
                    Response.status(Status.NOT_FOUND).entity("ID not found" + entity.id).build());
        }

        Panache.getEntityManager().merge(entity);
    }

    @GET
    @Path("{id}")
    public Movie find(@PathParam("id") @NotNull Long id) {
        Movie entity = Movie.findById(id);
        if (null == entity) {
            throw new WebApplicationException(
                    Response.status(Status.NOT_FOUND).entity("ID not found: " + String.valueOf(id)).build());
        }

        return entity;
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull Long id) {
        Movie entity = Movie.findById(id);
        if (null == entity) {
            throw new WebApplicationException(
                    Response.status(Status.NOT_FOUND).entity("ID not found: " + String.valueOf(id)).build());
        }

        entity.delete();
    }

    public Movie findByFilename(final String fileNameStart) {
        String fileName = removeFileSeparatorFromStartIfNeeded(fileNameStart);

        List<Movie> list = Movie.find("fileName", fileName).list();
        return (list.isEmpty() || list.size() > 1) ? null : list.get(0);
    }

    @POST
    @Path("filename")
    @Consumes(MediaType.TEXT_PLAIN)
    public Movie create(String newFile) {
        final Movie movie = Movie.create();
        final String baseFileName = new File(newFile).getName();
        final String name = baseFileName.substring(0, baseFileName.lastIndexOf("."));

        movie.fileName = newFile;
        movie.genreId = 0l;
        movie.serieId = 0l;
        movie.name = name;
        movie.pictureFileName = name + ".jpg";
        movie.displayOrder = 0;
        movie.persist();

        return movie;
    }

    private class Counter {
        private Integer i = 0;

        public Integer get() {
            return i;
        }

        public void inc() {
            this.i++;
        }
    }

    public String getNiceDisplayOrder(Movie movie) {
        final Counter i = new Counter();

        PanacheQuery<Movie> q = Movie.find("serieId", Sort.ascending("displayOrder"), movie.serieId);
        long count = q.count();

        q.list().stream().map(e -> (Movie) e).peek(m -> i.inc()).filter(m -> m.id.equals(movie.id)).findFirst();

        return String.format("%0" + String.valueOf(count).length() + "d", i.get());
    }

    public List<Movie> getRecentlyAdded() {
        return Movie.findAll(Sort.descending("id")).page(0, 64).list();
    }

    public static String removeFileSeparatorFromStartIfNeeded(String relative) {
        return !relative.startsWith(File.separator) ? relative : relative.substring(1);
    }

    public List<Movie> getNewReleases(int year) {
        List<Movie> movies = new ArrayList<>();

        Movie.findAll(Sort.ascending("genreId", "serieId", "displayOrder")).page(0, 50).stream().map(e -> (Movie) e)
                .filter((movie) -> (null != movie.year && movie.year >= (year - 3))).forEach((movie) -> {
                    movies.add(movie);
                });
        Collections.sort(movies, (Movie o1, Movie o2) -> o1.year.compareTo(o2.year) * -1);

        return movies;
    }
}
