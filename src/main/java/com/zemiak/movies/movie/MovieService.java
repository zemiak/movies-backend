package com.zemiak.movies.movie;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Stream;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
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

import com.zemiak.movies.batch.CacheClearEvent;
import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.serie.Serie;
import com.zemiak.movies.strings.Encodings;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class MovieService {
    @Inject
    MovieRepository repo;

    @GET
    @Path("all")
    public List<Movie> all() {
        return repo.listAll(Sort.ascending("displayOrder"));
    }

    @POST
    public Long create(@Valid @NotNull Movie entity) {
        if (null != entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID specified").build());
        }

        entity.persist();
        return entity.id;
    }

    @PUT
    public void update(@Valid @NotNull Movie entity) {
        if (null == entity.id) {
            throw new WebApplicationException(Response.status(Status.NOT_ACCEPTABLE).entity("ID not specified").build());
        }

        Movie findEntity = repo.findById(entity.id);
        if (null == findEntity) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not found" + entity.id).build());
        }

        Panache.getEntityManager().merge(entity);
    }

    @GET
    @Path("{id}")
    public Movie find(@PathParam("id") @NotNull Long id) {
        Movie entity = repo.findById(id);
        if (null == entity) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not found: " + String.valueOf(id)).build());
        }

        return entity;
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") @NotNull Long id) {
        Movie entity = repo.findById(id);
        if (null == entity) {
            throw new WebApplicationException(Response.status(Status.NOT_FOUND).entity("ID not found: " + String.valueOf(id)).build());
        }

        entity.delete();
    }

    public void clearCache(@Observes CacheClearEvent event) {
        Panache.getEntityManager().getEntityManagerFactory().getCache().evictAll();
    }

    @GET
    @Path("search/{pattern}")
    public List<Movie> getByExpression(@PathParam("pattern") @NotNull final String text) {
        List<Movie> res = new ArrayList<>();
        String textAscii = Encodings.toAscii(text.trim().toLowerCase());

        /**
         * TODO: optimize findAll() - either set page and size or do the filtering with SQL
         *
         * https://quarkus.io/guides/hibernate-orm-panache
         *
         * "You should only use list and stream methods if your table contains small enough data sets.
         * For larger data sets you can use the find method equivalents, which return a PanacheQuery
         * on which you can do paging"
         */
        Stream<Movie> stream = Movie.streamAll();
        stream.map(e -> (Movie) e).forEach(entry -> {
            String name = null == entry.name ? ""
                    : Encodings.toAscii(entry.name.trim().toLowerCase());
            if (name.contains(textAscii)) {
                res.add(entry);
            }
        });
        stream.close();

        return res;
    }

    @GET
    @Path("new")
    public List<Movie> getNewMovies() {
        return Movie.find("genre = :valueNew OR genre IS NULL",
            Sort.ascending("genre", "serie", "displayOrder"),
            Parameters.with("valueNew", Genre.findById(0)))
            .list();
    }

    @GET
    @Path("by-serie/{id}")
    public List<Movie> getSerieMovies(@PathParam("id") @NotNull Long id) {
        return Movie.find("serie = :valueNew OR serie IS NULL",
            Sort.ascending("displayOrder"),
            Parameters.with("valueNew", Serie.findById(id)))
            .list();
    }

    @GET
    @Path("by-genre/{id}")
    public List<Movie> getGenreMovies(@PathParam("id") @NotNull Long id) {
        return Movie.find("genre = :valueNew OR genre IS NULL",
            Sort.ascending("displayOrder"),
            Parameters.with("valueNew", Genre.findById(id)))
            .list();
    }

    @GET
    @Path("last/{count}")
    public List<Movie> getLastMovies(@PathParam("count") @NotNull Integer count) {
        return Movie.findAll(Sort.descending("id")).page(0, count).list();
    }

    public Movie findByFilename(final String fileNameStart) {
        String fileName = removeFileSeparatorFromStartIfNeeded(fileNameStart);

        List<Movie> list = Movie.find("fileName", fileName).list();
        return (list.isEmpty() || list.size() > 1) ? null : list.get(0);
    }

    public Movie create(String newFile) {
        final Movie movie = new Movie();
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

    public List<Movie> getNewReleases() {
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());

        List<Movie> movies = new ArrayList<>();
        // TODO: limit to 50 results
        Movie.findAll(Sort.ascending("genreId", "serieId", "displayOrder")).stream().map(e -> (Movie) e)
                .filter((movie) -> (null != movie.year && movie.year >= (cal.get(Calendar.YEAR) - 3)))
                .forEach((movie) -> {
                    movies.add(movie);
                });
        Collections.sort(movies, (Movie o1, Movie o2) -> o1.year.compareTo(o2.year) * -1);

        return movies;
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

        q.list().stream().map(e -> (Movie) e)
                .peek(m -> i.inc())
                .filter(m -> m.id.equals(movie.id))
                .findFirst();

        return String.format("%0" + String.valueOf(count).length() + "d", i.get());
    }

    public List<Movie> getRecentlyAdded() {
        return Movie.findAll(Sort.descending("id")).page(0, 64).list();
    }

    public static String removeFileSeparatorFromStartIfNeeded(String relative) {
        return !relative.startsWith(File.separator) ? relative : relative.substring(1);
    }
}
