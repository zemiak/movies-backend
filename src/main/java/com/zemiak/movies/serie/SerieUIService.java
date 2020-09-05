package com.zemiak.movies.serie;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.movie.MovieUIService;
import com.zemiak.movies.strings.Encodings;
import com.zemiak.movies.ui.FileUploadForm;
import com.zemiak.movies.ui.GuiDTO;
import com.zemiak.movies.ui.VaadingGridPagingResult;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import io.quarkus.panache.common.Sort;

@RequestScoped
@Path("series")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class SerieUIService {
    @Inject
    MovieUIService movies;

    @Inject
    SerieService series;

    @GET
    @Path("browse")
    public List<GuiDTO> getItemsForUI(@NotNull @QueryParam("id") final Long id) {
        return movies.getSerieMovies(id);
    }

    @GET
    @Path("search/{pattern}")
    public List<GuiDTO> getByExpression(@PathParam("pattern") @NotNull final String text) {
        List<GuiDTO> res = new ArrayList<>();
        String textAscii = Encodings.toAscii(text.trim().toLowerCase());

        Serie.traverse(Sort.ascending("id"), entry -> {
            String name = (null == entry.name ? "" : Encodings.toAscii(entry.name.trim().toLowerCase()));
            if (name.contains(textAscii)) {
                res.add(entry.toDto());
            }
        });

        return res;
    }

    @POST
    @Path("thumbnail")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadThumbnail(@MultipartForm FileUploadForm form) throws URISyntaxException {
        Serie entity = Serie.findById(form.getId());
        if (null == entity) {
            return Response.status(Status.NOT_FOUND).entity("Provided serie not found").build();
        }

        java.nio.file.Path path = Paths.get(ConfigurationProvider.getImgPath(), "serie", form.getId() + ".jpg");
        try {
            Files.write(path, form.getFileData());
        } catch (IOException e) {
            return Response.serverError().entity("Cannot write provided file").build();
        }

        entity.pictureFileName = entity.id + ".jpg";

        return Response
                .created(new URI(ConfigurationProvider.getExternalURL() + "/series/thumbnail?id=" + form.getId()))
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

        fileName = ConfigurationProvider.getImgPath() + "/serie/" + fileName;

        FileInputStream stream;
        try {
            stream = new FileInputStream(new File(fileName));
        } catch (FileNotFoundException e1) {
            return Response.status(Status.NOT_FOUND).entity("Thumbnail for " + id + " not found " + fileName).build();
        }

        return Response.ok(stream).header("Content-Disposition", "attachment; filename=" + e.pictureFileName).build();
    }

    protected Serie find(final Long id) {
        return series.find(id);
    }

    @GET
    @Path("count")
    public JsonObject count() {
        long count = Serie.count();
        return Json.createObjectBuilder().add("count", count).build();
    }

    @GET
    @Path("items")
    public VaadingGridPagingResult<SerieUI> getItems(@QueryParam("page") int page,
            @QueryParam("pageSize") int pageSize) {
        return new VaadingGridPagingResult<>(Serie.count(), Serie.findAll(Sort.by("displayOrder")).page(page, pageSize)
                .stream().map(SerieUI::of).collect(Collectors.toList()));
    }

    @GET
    @Path("detail/{id}")
    public SerieDetail findDetail(@PathParam("id") @NotNull final Long id) {
        Serie entity = Serie.findById(id);
        if (null == entity) {
            throw new WebApplicationException(
                    Response.status(Status.NOT_FOUND).entity("ID not found: " + String.valueOf(id)).build());
        }

        return SerieDetail.of(entity);
    }

    @GET
    @Path("detail/forNew")
    public SerieDetail findDetailAdd() {
        return SerieDetail.forNew();
    }
}
