package com.zemiak.movies.genre;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.movie.MovieUIService;
import com.zemiak.movies.serie.Serie;
import com.zemiak.movies.serie.SerieService;
import com.zemiak.movies.ui.FileUploadForm;
import com.zemiak.movies.ui.GuiDTO;
import com.zemiak.movies.ui.VaadingGridPagingResult;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("genres")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class GenreUIService {
    @Inject
    GenreService genres;

    @Inject
    SerieService series;

    @Inject
    MovieUIService movies;

    public List<GuiDTO> getRootItems() {
        var root = new ArrayList<GuiDTO>();
        Genre.traverse(Sort.ascending("id"), e -> {root.add(e.toDto());});
        
        root.add(Genre.getFreshGenre().toDto());
        root.add(Genre.getRecentlyAddedGenre().toDto());
        root.add(Genre.getUnassignedGenre().toDto());

        return root;
    }

    @GET
    @Path("search/{pattern}")
    public List<GuiDTO> getByExpression(@PathParam("pattern") @NotNull final String pattern) {
        return Genre.find("UPPER(name) LIKE ?1", "%" + pattern.toUpperCase() + "%").list().stream().map(e -> (Genre) e)
                .map(Genre::toDto).collect(Collectors.toList());
    }

    @POST
    @Path("thumbnail")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadThumbnail(@MultipartForm FileUploadForm form) throws URISyntaxException {
        Genre entity = Genre.findById(form.getId());
        if (null == entity) {
            return Response.status(Status.NOT_FOUND).entity("Provided genre not found").build();
        }

        java.nio.file.Path path = Paths.get(ConfigurationProvider.getImgPath(), "genre", form.getId() + ".jpg");
        try {
            Files.write(path, form.getFileData());
        } catch (IOException e) {
            return Response.serverError().entity("Cannot write provided file").build();
        }

        entity.pictureFileName = entity.id + ".jpg";

        return Response
                .created(new URI(ConfigurationProvider.getExternalURL() + "/genres/thumbnail?id=" + form.getId()))
                .build();
    }

    @GET
    @Path("thumbnail")
    public Response getThumbnail(@QueryParam("id") final Long id) {
        Genre e = Genre.isArtificial(id) ? Genre.findArtificial(id) : find(id);

        String fileName = e.pictureFileName;
        if (null == fileName) {
            return Response.status(Status.NOT_FOUND).entity("Thumbnail for " + id + " not yet created").build();
        }

        fileName = ConfigurationProvider.getImgPath() + "/genre/" + fileName;

        FileInputStream stream;
        try {
            stream = new FileInputStream(new File(fileName));
        } catch (FileNotFoundException e1) {
            return Response.status(Status.NOT_FOUND).entity("Thumbnail for " + id + " not found " + fileName).build();
        }

        return Response.ok(stream).header("Content-Disposition", "attachment; filename=" + e.pictureFileName).build();
    }

    protected Genre find(final Long id) {
        return genres.find(id);
    }

    @GET
    @Path("browse")
    public List<GuiDTO> getItemsForUI(@NotNull @QueryParam("id") final Long id) {
        if (Genre.ID_FRESH.equals(id)) {
            return getFreshMovies();
        }

        if (Genre.ID_UNASSIGNED.equals(id)) {
            return getUnassignedMovies();
        }

        if (Genre.ID_RECENTLY_ADDED.equals(id)) {
            return getRecentlyAddedMovies();
        }

        var results = new ArrayList<GuiDTO>();
        results.addAll(series.getGenreSeries(id).stream().map(Serie::toDto).collect(Collectors.toList()));
        results.addAll(movies.getGenreMovies(id));

        return results;
    }

    private List<GuiDTO> getUnassignedMovies() {
        return movies.getUnassignedMovies();
    }

    private List<GuiDTO> getRecentlyAddedMovies() {
        return movies.getRecentlyAddedMovies();
    }

    private List<GuiDTO> getFreshMovies() {
        int year = LocalDateTime.now().get(ChronoField.YEAR);
        return movies.getFreshMovies(year);
    }

    @GET
    @Path("count")
    public JsonObject count() {
        long count = Genre.count();
        return Json.createObjectBuilder().add("count", count).build();
    }

    @GET
    @Path("items")
    public VaadingGridPagingResult<GenreUI> getItems(@QueryParam("page") int page,
            @QueryParam("pageSize") int pageSize) {
        return new VaadingGridPagingResult<>(Genre.count(), Genre.findAll(Sort.by("displayOrder")).page(page, pageSize)
                .stream().map(GenreUI::of).collect(Collectors.toList()));
    }
}
