package com.zemiak.movies.service.scraper;

import com.zemiak.movies.batch.service.logs.BatchLogger;
import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.domain.UrlDTO;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Csfd implements IWebMetadataReader {
    private static final BatchLogger LOG = BatchLogger.getLogger(Csfd.class.getName());

    private static final String URL1 = "www.csfd.cz/";
    private static final String URL2 = "http://" + URL1;
    private static final String URL3 = "https://" + URL1;
    private static final String SEARCH_URL = URL2 + "hledat/?q=";

    private String imageFileName;

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

        Elements description = doc.select("div[data-truncate]");
        return null != description ? description.text() : "";
    }

    @Override
    public String getReaderName() {
        return "CSFD";
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

        Elements list = doc
                .select("div[id=search-films] > div[class=content] > ul[class=ui-image-list js-odd-even]");
        if (null == list) {
            return res;
        }

        Elements elements = list.select("li");

        elements.stream().forEach(li -> {
            final Elements href = li.select("div > h3 > a");
            if (null == href) {
                return;
            }

            final String desc = href.first().text();
            final String descUrl = href.first().absUrl("href");

            res.add(new UrlDTO(descUrl, getReaderName(), href.text(), desc));
        });

        return res;
    }

    @Override
    public void setImageFileName(final String imageFileName) {
        this.imageFileName = imageFileName;
    }

    @Override
    public void processThumbnail(final Movie movie) {
        Document doc = JsoupUtils.getMovieDocumentFromString(movie);
        if (null == doc) {
            return;
        }

        Element poster = doc.select("img[class=film-poster]").first();
        if (null == poster) {
            LOG.log(Level.SEVERE, "Cannot read poster", null);
            return;
        }

        String imageUrl = poster.attr("src");
        if (! imageUrl.startsWith("http")) {
            imageUrl = "http:" + imageUrl;
        }

        try {
            downloadFile(imageUrl, imageFileName);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Cannot fetch poster url {0} file name {1} error {2}",
                    new Object[]{imageUrl, imageFileName, ex});
        }
    }

    public void downloadFile(final String imageUrl, final String fileLocation) throws IOException {
        Connection.Response resultImageResponse = Jsoup.connect(imageUrl).ignoreContentType(true).execute();

        try (FileOutputStream out = new FileOutputStream(new java.io.File(fileLocation))) {
            out.write(resultImageResponse.bodyAsBytes());
        }
    }

    @Override
    public Integer parseYear(final Movie movie) {
        if (null == movie.getWebPage()) {
            return null;
        }

        Document doc = JsoupUtils.getMovieDocumentFromString(movie);
        if (null == doc) {
            return null;
        }

        Element origin = doc.select("p[class=origin]").first();
        if (null == origin) {
            LOG.log(Level.SEVERE, "Cannot read origin", null);
            return null;
        }

        String originText = origin.text();
        if (null == originText || "".equals(originText) || !originText.contains(",")) {
            LOG.log(Level.SEVERE, "Bad format of origin. Should be country, year, length", originText);
            return null;
        }

        String[] originData = originText.split(",");
        if (3 != originData.length) {
            LOG.log(Level.SEVERE, "Bad format of origin. Should be country, year, length", originText);
            return null;
        }

        return Integer.valueOf(originData[1].trim());
    }

    @Override
    public String getWebPage(final Movie movie) {
        Document doc = JsoupUtils.getMovieDocument(movie);
        if (null == doc) {
            return null;
        }

        return doc.toString();
    }
}
