package com.zemiak.movies.batch.metadata;

import com.zemiak.movies.batch.service.logs.BatchLogger;
import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.service.scraper.WebMetadataReader;
import java.util.logging.Level;

public class MovieMetadata {
    private static final BatchLogger LOG = BatchLogger.getLogger(MovieMetadata.class.getName());
    private static final WebMetadataReader DESCRIPTIONS = new WebMetadataReader(null, null, null, true);

    private String encoder;
    private String name;
    private String artist;
    private String albumArtist;
    private String composer;
    private String albumName;
    private String grouping;
    private String genre;
    private Integer year;
    private String comments;
    private String niceDisplayOrder;

    private Movie movie;

    public MovieMetadata(final Movie movie) {
        this.movie = movie;
    }

    public MovieMetadata() {
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getEncoder() {
        return encoder;
    }

    public void setEncoder(String encoder) {
        this.encoder = encoder;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getComposer() {
        return composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getGrouping() {
        return grouping;
    }

    public void setGrouping(String grouping) {
        this.grouping = grouping;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "MovieMetadata{" + "genre=" + genre + ", name=" + name + ", comments=" + comments + '}';
    }

    private boolean commentsShouldBeUpdated0(final boolean debug) {
        boolean commentsAreEmpty = (comments == null
                || comments.trim().isEmpty()
                || "''".equals(comments));

        if (commentsAreEmpty && movie.isDescriptionEmpty() && !movie.isUrlEmpty()) {
            if (debug) {
                LOG.log(Level.INFO, "{0}: metadata and movie comments empty, comments URL not. Updating.", movie.getFileName());
            }

            return true;
        }

        return false;
    }

    public boolean commentsShouldBeUpdated() {
        return commentsShouldBeUpdated0(true);
    }

    public boolean commentsShouldBeUpdatedQuiet() {
        return commentsShouldBeUpdated0(false);
    }

    public boolean isNameEqual() {
        if (name == null && movie.getName() != null) {
            return false;
        }

        String shouldBeName = getMovieName();

        return name == null || name.equals(shouldBeName);
    }

    public boolean isYearEqual() {
        if (year == null && movie.getYear() != null) {
            return false;
        }

        return year == null || year.equals(movie.getYear());
    }

    public boolean isGenreEqual() {
        if (genre == null) {
            return false;
        }

        return genre.equals(movie.composeGenreName());
    }

    private boolean isMetadataEqual0(final boolean debug) {
        boolean ret = true;

        if (! isNameEqual()) {
            ret = false;

            if (debug) {
                LOG.log(Level.INFO, "{0}: isMetadataEqual: Name is not equal. Is \"{1}\", should be \"{2}\"",
                        new Object[]{movie.getFileName(), name, getMovieName()});
            }
        }

        if (! isGenreEqual()) {
            ret = false;

            if (debug) {
                LOG.log(Level.INFO, "{0}: isMetadataEqual: Genre is not equal. Is \"{1}\", should be \"{2}\"",
                        new Object[]{movie.getFileName(), genre, movie.composeGenreName()});
            }
        }

        if (! isYearEqual()) {
            ret = false;

            if (debug) {
                LOG.log(Level.INFO, "{0}: isMetadataEqual: Year is not equal. Is \"{1}\", should be \"{2}\"",
                        new Object[]{movie.getFileName(), year, String.valueOf(movie.getYear())});
            }
        }

        if (commentsShouldBeUpdated0(debug)) {
            ret = false;
        }

        return ret;
    }

    public boolean isMetadataEqual() {
        return isMetadataEqual0(true);
    }

    public Movie getMovie() {
        return movie;
    }

    public String getMovieName() {
        String title = null == movie.getName() || movie.getName().trim().isEmpty() ? movie.getOriginalName() : movie.getName();
        if (null != movie.getYear() && movie.getYear() > 0 && (null == movie.getSerie() || movie.getSerie().isEmpty())) {
            title += " (" + String.valueOf(movie.getYear()) + ")";
        }

        return title;
    }

    public String getNiceDisplayOrder() {
        return niceDisplayOrder;
    }

    public void setNiceDisplayOrder(String niceDisplayOrder) {
        this.niceDisplayOrder = niceDisplayOrder;
    }


}
