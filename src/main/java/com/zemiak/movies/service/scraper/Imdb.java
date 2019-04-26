package com.zemiak.movies.service.scraper;

import com.zemiak.movies.batch.service.logs.BatchLogger;
import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.domain.UrlDTO;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Imdb implements IWebMetadataReader {
    private static final BatchLogger LOG = BatchLogger.getLogger(Imdb.class.getName());
    private String imageFileName;

    private static final String URL1 = "www.imdb.com/";
    private static final String URL2 = "http://" + URL1;
    private static final String URL3 = "https://" + URL1;
    private static final String SEARCH_URL = URL2 + "find?q=";

    @Override
    public boolean accepts(final Movie movie) {
        final String url = movie.getUrl();
        return (null != url) && (url.startsWith(URL1) || url.startsWith(URL2) || url.startsWith(URL3));
    }

    @Override
    public String parseDescription(final Movie movie) {
        Document doc = JsoupUtils.getMovieDocumentFromString(movie);
        if (null == doc) {
            return null;
        }

        Elements description = doc.select("meta[name=description]");
        return null != description ? description.attr("content") : "";
    }

    @Override
    public String getReaderName() {
        return "IMDB";
    }

    @Override
    public List<UrlDTO> getUrlCandidates(final String movieName) {
        List<UrlDTO> res = new ArrayList<>();
        String url;

        try {
            url = SEARCH_URL + URLEncoder.encode(movieName, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, "Unsupported UTF-8 encoding.", ex);
            return res;
        }

        Document doc = JsoupUtils.getDocument(url);
        if (null == doc) {
            return res;
        }

        Elements results = doc.select("td[class=result_text]");
        if (null == results) {
            return res;
        }

        results.stream().forEach(result -> {
            Elements result2 = result.select("a");
            if (null == result2) {
                return;
            }

            Element href = result2.first();
            res.add(new UrlDTO(href.absUrl("href"), getReaderName(), href.text(), result.text()));
        });

        return res;
    }

    @Override
    public void setImageFileName(final String imageFileName) {
        this.imageFileName = imageFileName;
    }

    @Override
    public void processThumbnail(Movie movie) {
        Document doc = JsoupUtils.getMovieDocumentFromString(movie);
        if (null == doc) {
            return;
        }

        Element poster = doc.select("div[class=poster]>a>img").first();
        if (null == poster) {
            LOG.log(Level.SEVERE, "Cannot read poster", null);
            return;
        }

        String imageUrl = poster.attr("src");

        try {
            new Csfd().downloadFile(imageUrl, imageFileName);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot fetch poster url {0} file name {1} error {2}",
                    new Object[]{imageUrl, imageFileName, ex});
        }
    }

    @Override
    public String getWebPage(Movie movie) {
        Document doc = JsoupUtils.getMovieDocument(movie);
        if (null == doc) {
            return null;
        }

        return doc.toString();
    }

    @Override
    public Integer parseYear(final Movie movie) {
        Document doc = JsoupUtils.getMovieDocumentFromString(movie);
        if (null == doc) {
            return null;
        }

        Element datePublished = doc.select("span[id=titleYear]>a").first();
        if (null == datePublished) {
            LOG.log(Level.SEVERE, "Cannot read origin", null);
            return null;
        }

        String dateText = datePublished.text();
        if (null == dateText || dateText.length() != 4) {
            LOG.log(Level.SEVERE, "Bad format of date text (1). Should be yyyy, is " + dateText, dateText);
            return null;
        }

        return Integer.valueOf(dateText);
    }
}
