package com.zemiak.movies.itunes;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.zemiak.movies.scraper.UrlDTO;

import org.eclipse.microprofile.rest.client.RestClientBuilder;

@Dependent
public class ItunesArtworkService {
    private static final String URL_STRING = "http://ax.itunes.apple.com";
    private static final String COUNTRY_US = "us";
    private static final String ENTITY_MOVIE = "movie";

    protected JsonObject getMovieArtworkResultsJson(String movieName) {
        ItunesArtworkRestClient client = getRestClient();
        Response response = client.wsSearch(COUNTRY_US, ENTITY_MOVIE, movieName);

        if (!Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
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

    public Set<UrlDTO> getMovieArtworkResults(String movieName) {
        JsonObject results = getMovieArtworkResultsJson(movieName);
        JsonArray entries = results.getJsonArray("results");

        if (results.getInt("resultCount", 0) == 0 || null == entries || entries.isEmpty()) {
            return Collections.emptySet();
        }

        return entries.stream().map(e -> ItunesArtwork.mapFromEntry(e)).collect(Collectors.toSet());
    }

    public byte[] getMovieArtworkWithDimension(UrlDTO artwork, int dimension) {
        String url = artwork.imageUrl;
        url = url.replace("100x100", String.format("%dx%d", dimension, dimension));

        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(url);
        Response response = target.request().head();
        if (!Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            client.close();
            return null;
        }

        response = target.request().get();
        if (!Response.Status.Family.SUCCESSFUL.equals(response.getStatusInfo().getFamily())) {
            client.close();
            return null;
        }

        InputStream stream = response.readEntity(InputStream.class);

        byte[] res;
        try {
            res = stream.readAllBytes();
        } catch (IOException e) {
            // pass
            res = null;
        } finally {
            client.close();
        }

        return res;
    }

    public byte[] getMovieArtwork(UrlDTO artwork) {
        return getMovieArtworkWithDimension(artwork, 1024);
    }
}
