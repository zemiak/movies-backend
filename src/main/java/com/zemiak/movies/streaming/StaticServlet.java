package com.zemiak.movies.streaming;

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
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import com.zemiak.movies.genre.Genre;
import com.zemiak.movies.config.ConfigurationProvider;
import com.zemiak.movies.movie.Movie;
import com.zemiak.movies.serie.Serie;

/**
 * Based on http://bruno.defraine.net/StaticServlet.java
 */
@WebServlet(urlPatterns = {"/images/*"})
public class StaticServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final String imgPath;

    public StaticServlet() {
        imgPath = ConfigurationProvider.getImgPath();
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
            Movie movie = Movie.findById(id);
            if (null == movie) {
                throw new WebApplicationException("Unknown movie ID: " + id, Status.NOT_FOUND);
            }

            return "/movie/" + movie.pictureFileName;
        }

        private String serieFileName(Integer id) {
            Serie serie = Serie.findById(id);
            if (null == serie) {
                throw new WebApplicationException("Unknown serie ID: " + id, Status.NOT_FOUND);
            }

            return "/serie/" + serie.pictureFileName;
        }

        private String genreFileName(Integer id) {
            Genre genre = Genre.findById(id);
            if (null == genre) {
                throw new WebApplicationException("Unknown genre ID: " + id, Status.NOT_FOUND);
            }

            return "/genre/" + genre.pictureFileName;
        }
    }
}
