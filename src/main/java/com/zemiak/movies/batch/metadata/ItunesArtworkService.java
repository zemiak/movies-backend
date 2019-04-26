package com.zemiak.movies.batch.metadata;

import com.zemiak.movies.domain.ItunesArtwork;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class ItunesArtworkService {
    private static final String URI = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/wa/wsSearch";
    private static final String COUNTRY_US = "us";
    private static final String ENTITY_MOVIE = "movie";
    WebTarget target;
    private static final List<Integer> DIMENSIONS = Arrays.asList(2048, 1024, 600, 100);

    public ItunesArtworkService() {
        target = ClientBuilder.newClient().target(URI);
    }

    private JsonObject getMovieArtworkResultsJson(String movieName) {
        Response response = target
                .queryParam("country", COUNTRY_US)
                .queryParam("entity", ENTITY_MOVIE)
                .queryParam("term", movieName)
                .request(MediaType.APPLICATION_JSON).get();

        if (! Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            throw new IllegalStateException("Response was: " + response.getStatus() + " not Success");
        }

        return response.readEntity(JsonObject.class);
    }

    public Set<ItunesArtwork> getMovieArtworkResults(String movieName) {
        JsonObject results = getMovieArtworkResultsJson(movieName);
        JsonArray entries = results.getJsonArray("results");

        if (results.getInt("resultCount", 0) == 0 || null == entries || entries.isEmpty()) {
            return Collections.EMPTY_SET;
        }

        return entries.stream().map(ItunesArtwork::mapFromEntry).collect(Collectors.toSet());
    }

    public InputStream getMovieArtworkWithDimension(ItunesArtwork artwork, int dimension) {
        String url = artwork.getArtworkUrl100();
        url = url.replace("100x100", String.format("%dx%d", dimension, dimension));

        Response response = ClientBuilder.newClient().target(url).request().get();
        if (! Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            return null;
        }

        return response.readEntity(InputStream.class);
    }

    public InputStream getMovieArtwork(ItunesArtwork artwork) {
        return DIMENSIONS.stream()
                .map(dimension -> getMovieArtworkWithDimension(artwork, dimension))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }
}
