package com.zemiak.movies.metadata;

import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.zemiak.movies.scraper.ItunesArtwork;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

public class ItunesArtworkService {
    private static final String URL_STRING = "http://ax.itunes.apple.com";
    private static final String COUNTRY_US = "us";
    private static final String ENTITY_MOVIE = "movie";

    protected JsonObject getMovieArtworkResultsJson(String movieName) {
        ItunesArtworkRestClient client = getRestClient();
        Response response = client.wsSearch(COUNTRY_US, ENTITY_MOVIE, movieName);

        if (! Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            throw new IllegalStateException("Response was: " + response.getStatus() + " not Success");
        }

        String body = response.readEntity(String.class);
        return Json.createReader(new StringReader(body)).readObject();
    }

    private ItunesArtworkRestClient getRestClient() {
        URL url;

        try {
            url = new URL(URL_STRING);
        } catch (MalformedURLException e) {
            throw new RuntimeException("URL invalid: " + URL_STRING);
        }

        return RestClientBuilder.newBuilder().baseUrl(url).build(ItunesArtworkRestClient.class);
    }

    public Set<ItunesArtwork> getMovieArtworkResults(String movieName) {
        JsonObject results = getMovieArtworkResultsJson(movieName);
        JsonArray entries = results.getJsonArray("results");

        if (results.getInt("resultCount", 0) == 0 || null == entries || entries.isEmpty()) {
            return Collections.emptySet();
        }

        return entries.stream().map(ItunesArtwork::mapFromEntry).collect(Collectors.toSet());
    }

    public InputStream getMovieArtworkWithDimension(ItunesArtwork artwork, int dimension) {
        String url = artwork.getArtworkUrl100();
        url = url.replace("100x100", String.format("%dx%d", dimension, dimension));

        WebTarget target = ClientBuilder.newClient().target(url);
        Response response = target.request().head();
        if (! Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            return null;
        }

        response = target.request().get();
        if (! Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            return null;
        }

        return response.readEntity(InputStream.class);
    }

    public InputStream getMovieArtwork(ItunesArtwork artwork) {
        return getMovieArtworkWithDimension(artwork, 1024);
    }
}
