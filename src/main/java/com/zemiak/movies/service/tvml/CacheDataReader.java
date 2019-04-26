package com.zemiak.movies.service.tvml;

import com.zemiak.movies.domain.Genre;
import com.zemiak.movies.domain.Serie;
import com.zemiak.movies.service.GenreService;
import com.zemiak.movies.service.MovieService;
import com.zemiak.movies.service.SerieService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

@Singleton
public class CacheDataReader {
    private static final Logger LOG = Logger.getLogger(CacheDataReader.class.getName());

    @Inject
    MovieService movies;

    @Inject
    SerieService series;

    @Inject
    GenreService genres;

    @Inject
    TvmlReader reader;

    private String version;
    private JsonObject cache;

    @PostConstruct
    public void clear() {
        buildVersion();
        buildCache();
    }

    public String getVersion() {
        if (null == version) {
            buildVersion();
        }

        return version;
    }

    private void buildVersion() {
        final MessageDigest digest = getInitialDigest();
        if (null == digest) {
            version = "err-digest";
            return;
        }

        movies.all().stream().
                map(m -> m.getFileName() + m.getSerieName() + m.getGenreName() + m.getDisplayOrder() + m.getYear() + m.getName() + m.getDescription())
                .forEach(s -> digest.update(s.getBytes(StandardCharsets.UTF_8)));

        version = bytesToHex(digest.digest());
    }

    private MessageDigest getInitialDigest() {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            LOG.log(Level.SEVERE, "SHA-256 not supported!", ex);
            return null;
        }

        return digest;
    }

    public JsonObject getCache() {
        if (null == cache) {
            buildCache();
        }

        return cache;
    }

    private void buildCache() {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        genres.all()
                .stream()
                .forEach(item -> this.processGenre(item, builder));

        series.all()
                .stream()
                .forEach(item -> this.processSerie(item, builder));

        processLatestReleases(builder);
        processRecentlyAdded(builder);
        processNoGenreMovies(builder);
        processRoot(builder);

        cache = builder.build();
    }

    private void processGenre(Genre item, JsonObjectBuilder builder) {
        String folder = "g" + item.getId();
        TvmlData data = reader.readData(folder);
        builder.add(folder, dataToJson(data));
    }

    private void processSerie(Serie item, JsonObjectBuilder builder) {
        String folder = "s" + item.getId();
        TvmlData data = reader.readData(folder);
        builder.add(folder, dataToJson(data));
    }

    private void processLatestReleases(JsonObjectBuilder builder) {
        String folder = "g:rel";
        TvmlData data = reader.readData(folder);
        builder.add(folder, dataToJson(data));
    }

    private void processRecentlyAdded(JsonObjectBuilder builder) {
        String folder = "g:add";
        TvmlData data = reader.readData(folder);
        builder.add(folder, dataToJson(data));
    }

    private void processNoGenreMovies(JsonObjectBuilder builder) {
        String folder = "g:none";
        TvmlData data = reader.readData(folder);
        builder.add(folder, dataToJson(data));
    }

    private void processRoot(JsonObjectBuilder builder) {
        String folder = "/";
        TvmlData data = reader.readData(folder);
        builder.add(folder, dataToJson(data));
    }

    public void reload() {
        buildVersion();
        buildCache();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte byt : bytes) {
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        }

        return result.toString();
     }

    private JsonObjectBuilder dataToJson(TvmlData data) {
        return Json.createObjectBuilder()
                .add("folders", buildFolders(data.getFolders()))
                .add("movies", buildMovies(data.getMovies()))
                .add("title", data.getTitle());
    }

    private JsonArrayBuilder buildFolders(List<FolderData> folders) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (FolderData folder: folders) {
            builder = builder.add(Json.createObjectBuilder()
                    .add("name", nullSafe(folder.getName()))
                    .add("path", nullSafe(folder.getPath()))
                    .add("displayOrder", null == folder.getDisplayOrder() ? 99000 : folder.getDisplayOrder())
            );
        }

        return builder;
    }

    private JsonArrayBuilder buildMovies(List<MovieData> movies) {
        JsonArrayBuilder builder = Json.createArrayBuilder();
        for (MovieData movie: movies) {
            builder = builder.add(Json.createObjectBuilder()
                    .add("name", nullSafe(movie.getName()))
                    .add("path", nullSafe(movie.getPath()))
                    .add("displayOrder", null == movie.getDisplayOrder() ? 99000 : movie.getDisplayOrder())
                    .add("description", nullSafe(movie.getDescription()))
                    .add("year", nullSafe(movie.getYear()))
                    .add("genreName", movie.getGenreName())
                    .add("serieName", movie.getSerieName())
                    .add("id", movie.getId())
                    .add("genreKey", movie.getGenreKey())
                    .add("serieKey", movie.getSerieKey())
            );
        }

        return builder;
    }

    private String nullSafe(String value) {
        return null == value ? "" : value;
    }
}
