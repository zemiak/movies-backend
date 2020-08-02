package com.zemiak.movies.imdb;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@RequestScoped
@Path("metadata/imdb")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ImdbResource {
    @Inject
    Imdb service;

    @Inject
    DownloadFile downloader;

    @GET
    @Path("{pattern}")
    public List<JsonObject> getByExpression(@PathParam("pattern") @NotNull final String pattern) {
        String patternAscii = Encodings.toAscii(pattern.trim().toLowerCase());
        return service.getUrlCandidates(patternAscii).stream().map(UrlDTO::toJson).collect(Collectors.toList());
    }

    @PUT
    @Path("{id}")
    public Response uploadThumbnailUrl(@PathParam("id") Long id, JsonObject body)
            throws URISyntaxException {
        Movie entity = Movie.findById(id);
        if (null == entity) {
            return Response.status(Status.NOT_FOUND).entity("Provided movie not found").build();
        }

        if (null == body || !body.containsKey("url") || !body.containsKey("description") || !body.containsKey("imageUrl")) {
            return Response.status(Status.EXPECTATION_FAILED).entity("Provide fields: url, description, imageUrl, year (not mandatory)").build();
        }

        URL url;
        try {
            url = new URL(String.valueOf(body.getString("url")));
        } catch (MalformedURLException e1) {
            return Response.status(Status.EXPECTATION_FAILED).entity("Provided url is invalid").build();
        }

        Document doc;
        try {
            doc = Jsoup.connect(url.toString()).get();
        } catch (IOException e1) {
            return Response.status(Status.BAD_GATEWAY).entity("Provided url cannot be downloaded").build();
        }

        String imageUri = service.getImageUrl(doc);
        java.nio.file.Path path = Paths.get(ConfigurationProvider.getImgPath(), "movie", id + ".jpg");
        URL imageUrl;
        try {
            imageUrl = new URL(String.valueOf(imageUri));
        } catch (MalformedURLException e1) {
            return Response.status(Status.EXPECTATION_FAILED).entity("Image URL is invalid: " + imageUri).build();
        }

        Response response = downloader.download(imageUrl, path);
        if (!Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            return response;
        }

        entity.pictureFileName = entity.id + ".jpg";
        entity.description = service.getDescription(doc);
        entity.year = service.getYear(doc);

        return Response
            .status(Status.OK)
            .entity(Movie.toJson(entity))
            .build();
    }
}
