package com.zemiak.movies.servlet;

import com.zemiak.movies.domain.Genre;
import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.domain.Serie;
import com.zemiak.movies.lookup.CDILookup;
import com.zemiak.movies.service.ConfigurationProvider;
import com.zemiak.movies.service.GenreService;
import com.zemiak.movies.service.MovieService;
import com.zemiak.movies.service.SerieService;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Based on http://bruno.defraine.net/StaticServlet.java
 */
@WebServlet(urlPatterns = {"/images/*"})
public class StaticServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final String imgPath;
    private final MovieService movies;
    private final SerieService series;
    private final GenreService genres;

    public StaticServlet() {
        imgPath = ConfigurationProvider.getImgPath();
        movies = new CDILookup().lookup(MovieService.class);
        series = new CDILookup().lookup(SerieService.class);
        genres = new CDILookup().lookup(GenreService.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            StaticFile file = new StaticFile(req);
            file.respondGet(resp);
        } catch (IllegalStateException ex) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            StaticFile file = new StaticFile(req);
            file.setHeaders(resp);
        } catch (IllegalStateException ex) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        }
    }

    private class StaticFile {
        private final String mimeType;
        private final long contentLength;
        private final Path path;

        StaticFile(HttpServletRequest req) {
            // /movie/id, /serie/id, /genre/id
            String imageFileName;

            try {
                if (req.getPathInfo().startsWith("/movie/")) {
                    imageFileName = movieFileName(Integer.valueOf(req.getPathInfo().substring(7)));
                } else if (req.getPathInfo().startsWith("/serie/")) {
                    imageFileName = serieFileName(Integer.valueOf(req.getPathInfo().substring(7)));
                } else if (req.getPathInfo().startsWith("/genre/")) {
                    imageFileName = genreFileName(Integer.valueOf(req.getPathInfo().substring(7)));
                } else {
                    throw new IllegalStateException("Not a valid image URL format: " + req.getPathInfo());
                }
            } catch (NumberFormatException ex) {
                throw new IllegalStateException("Invalid ID format: " + req.getPathInfo());
            }

            path = Paths.get(imgPath, imageFileName);
            String pathString = path.toString();
            mimeType = null == getServletContext().getMimeType(pathString) ? "application/octet-stream" : getServletContext().getMimeType(pathString);

            File f = new File(pathString);
            if (!f.isFile()) {
                throw new IllegalStateException("Not a file: " + pathString);
            }

            contentLength = f.length();
        }

        public void setHeaders(HttpServletResponse resp) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(mimeType);
            resp.setContentLengthLong(contentLength);
        }

        public void respondGet(HttpServletResponse resp) throws IOException {
            setHeaders(resp);
            final OutputStream os;
            os = resp.getOutputStream();
            Files.copy(path, os);
        }

        private String movieFileName(Integer id) {
            Movie movie = movies.find(id);
            if (null == movie) {
                throw new IllegalStateException("Unknown movie ID: " + id);
            }

            return "/movie/" + movie.getPictureFileName();
        }

        private String serieFileName(Integer id) {
            Serie serie = series.find(id);
            if (null == serie) {
                throw new IllegalStateException("Unknown serie ID: " + id);
            }

            return "/serie/" + serie.getPictureFileName();
        }

        private String genreFileName(Integer id) {
            Genre genre = genres.find(id);
            if (null == genre) {
                throw new IllegalStateException("Unknown genre ID: " + id);
            }

            return "/genre/" + genre.getPictureFileName();
        }
    }
}
