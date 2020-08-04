package com.zemiak.movies.itunes;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.movie.DownloadFile;
import com.zemiak.movies.movie.Movie;
import com.zemiak.movies.scraper.UrlDTO;
import com.zemiak.movies.strings.Encodings;

@RequestScoped
@Path("metadata/itunes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ItunesResource {
    @Inject
    ItunesArtworkService service;

    @Inject
    DownloadFile downloader;

    @GET
    @Path("{pattern}")
    public Set<UrlDTO> getByExpression(@PathParam("pattern") @NotNull final String pattern) {
        String patternAscii = Encodings.toAscii(pattern.trim().toLowerCase());
        return service.getMovieArtworkResults(patternAscii);
    }

    @PUT
    @Path("{id}")
    @Transactional
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

        java.nio.file.Path path = Paths.get(ConfigurationProvider.getImgPath(), "movie", id + ".jpg");

        Response response = downloader.download(url, path);
        if (Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            return response;
        }

        entity.pictureFileName = entity.id + ".jpg";

        return Response
            .status(Status.OK)
            .entity(Movie.toJson(entity))
            .build();
    }
}
