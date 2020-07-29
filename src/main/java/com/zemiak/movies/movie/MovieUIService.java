package com.zemiak.movies.movie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
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

import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.strings.Encodings;
import com.zemiak.movies.ui.FileUploadForm;
import com.zemiak.movies.ui.GuiDTO;
import com.zemiak.movies.ui.VaadingGridPagingResult;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import io.quarkus.panache.common.Parameters;
import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class MovieUIService {
    @Inject
    MovieService movies;

    @GET
    @Path("search/{pattern}")
    public List<GuiDTO> getByExpression(@PathParam("pattern") @NotNull final String text) {
        List<GuiDTO> res = new ArrayList<>();
        String textAscii = Encodings.toAscii(text.trim().toLowerCase());

        /**
         * TODO: optimize findAll() - either set page and size or do the filtering with
         * SQL
         *
         * https://quarkus.io/guides/hibernate-orm-panache
         *
         * "You should only use list and stream methods if your table contains small
         * enough data sets. For larger data sets you can use the find method
         * equivalents, which return a PanacheQuery on which you can do paging"
         */
        Stream<Movie> stream = Movie.streamAll();
        stream.map(e -> (Movie) e).forEach(entry -> {
            String name = null == entry.name ? "" : Encodings.toAscii(entry.name.trim().toLowerCase());
            if (name.contains(textAscii)) {
                res.add(entry.toDto());
            }
        });
        stream.close();

        return res;
    }

    @POST
    @Path("thumbnail")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadThumbnail(@MultipartForm FileUploadForm form) throws URISyntaxException {
        Movie entity = Movie.findById(form.getId());
        if (null == entity) {
            return Response.status(Status.NOT_FOUND).entity("Provided movie not found").build();
        }

        java.nio.file.Path path = Paths.get(ConfigurationProvider.getImgPath(), "movie", form.getId() + ".jpg");
        try {
            Files.write(path, form.getFileData());
        } catch (IOException e) {
            return Response.serverError().entity("Cannot write provided file").build();
        }

        entity.pictureFileName = entity.id + ".jpg";

        return Response
                .created(new URI(ConfigurationProvider.getExternalURL() + "/movies/thumbnail?id=" + form.getId()))
                .build();
    }

    @POST
    @Path("{id}/thumbnail/url")
    public Response uploadThumbnailUrl(@PathParam("id") Long id, JsonObject body)
            throws URISyntaxException {
        Movie entity = Movie.findById(id);
        if (null == entity) {
            return Response.status(Status.NOT_FOUND).entity("Provided movie not found").build();
        }

        if (null == body || !body.containsKey("url")) {
            return Response.status(Status.EXPECTATION_FAILED).entity("Provide an URL").build();
        }

        URL url;
        try {
            url = new URL(String.valueOf(body.getString("url")));
        } catch (MalformedURLException e1) {
            return Response.status(Status.EXPECTATION_FAILED).entity("Provided url is invalid").build();
        }

        DownloadClient client = RestClientBuilder.newBuilder().baseUrl(url).build(DownloadClient.class);
        Response response = client.download();
        if (200 != response.getStatus()) {
            return Response.status(Status.BAD_GATEWAY).entity("Provided url cannot be downloaded").build();
        }

        java.nio.file.Path path = Paths.get(ConfigurationProvider.getImgPath(), "movie", id + ".jpg");
        InputStream stream = response.readEntity(InputStream.class);
        try {
            Files.write(path, stream.readAllBytes());
        } catch (IOException e) {
            return Response.serverError().entity("Cannot write provided file").build();
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                // pass
            }

            response.close();
        }

        entity.pictureFileName = entity.id + ".jpg";

        return Response
                .created(new URI(ConfigurationProvider.getExternalURL() + "/movies/thumbnail?id=" + id))
                .build();
    }

    @GET
    @Path("thumbnail")
    public Response getThumbnail(@QueryParam("id") final Long id) {
        var e = find(id);
        String fileName = e.pictureFileName;

        if (null == fileName) {
            return Response.status(Status.NOT_FOUND).entity("Thumbnail for " + id + " not yet created").build();
        }

        fileName = ConfigurationProvider.getImgPath() + "/movie/" + fileName;
        FileInputStream stream;
        try {
            stream = new FileInputStream(new File(fileName));
        } catch (FileNotFoundException e1) {
            return Response.status(Status.NOT_FOUND).entity("Thumbnail for " + id + " not found " + fileName).build();
        }

        return Response.ok(stream).header("Content-Disposition", "attachment; filename=" + e.pictureFileName).build();
    }

    protected Movie find(final Long id) {
        return movies.find(id);
    }

    public List<GuiDTO> getRecentlyAddedMovies() {
        return Movie.findAll(Sort.descending("id")).page(0, 50).list().stream().map(e -> (Movie) e).map(Movie::toDto)
                .collect(Collectors.toList());
    }

    public List<GuiDTO> getFreshMovies(int year) {
        return movies.getNewReleases(year).stream().map(Movie::toDto).collect(Collectors.toList());
    }

    public List<GuiDTO> getSerieMovies(final Long id) {
        return Movie.find("serieId = :serieId", Sort.ascending("displayOrder"), Parameters.with("serieId", id)).list()
                .stream().map(e -> (Movie) e).map(Movie::toDto).collect(Collectors.toList());
    }

    public List<GuiDTO> getGenreMovies(final Long id) {
        return Movie
                .find("genreId = :genreId and (serieId = NULL or serieId = 0)", Sort.ascending("displayOrder"),
                        Parameters.with("genreId", id))
                .list().stream().map(e -> (Movie) e).map(Movie::toDto).collect(Collectors.toList());
    }

    public List<GuiDTO> getUnassignedMovies() {
        return Movie
                .find("genreId = :genreId OR genreId IS NULL", Sort.ascending("displayOrder"),
                        Parameters.with("genreId", Genre.ID_NONE))
                .list().stream().map(e -> (Movie) e).map(Movie::toDto).collect(Collectors.toList());
    }

    @GET
    @Path("count")
    public JsonObject count() {
        long count = Movie.count();
        return Json.createObjectBuilder().add("count", count).build();
    }

    @GET
    @Path("items")
    public VaadingGridPagingResult<MovieUI> getItems(@QueryParam("page") int page,
            @QueryParam("pageSize") int pageSize) {
        return new VaadingGridPagingResult<>(Movie.count(), Movie.findAll(Sort.by("displayOrder")).page(page, pageSize)
                .stream().map(MovieUI::of).collect(Collectors.toList()));
    }

    @GET
    @Path("detail/{id}")
    public MovieDetail findDetail(@PathParam("id") @NotNull final Long id) {
        Movie entity = Movie.findById(id);
        if (null == entity) {
            throw new WebApplicationException(
                    Response.status(Status.NOT_FOUND).entity("ID not found: " + String.valueOf(id)).build());
        }

        return MovieDetail.of(entity);
    }

    @GET
    @Path("detail/forNew")
    public MovieDetail findDetailAdd() {
        return MovieDetail.forNew();
    }
}
