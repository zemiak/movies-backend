package com.zemiak.movies.imdb;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.enterprise.context.Dependent;

import com.zemiak.movies.scraper.JsoupUtils;
import com.zemiak.movies.scraper.UrlDTO;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Dependent
public class Imdb {
    private static final Logger LOG = Logger.getLogger(Imdb.class.getName());
    private static final String SEARCH_URL = "https://www.imdb.com/find?q=";
    public static Pattern NUMBER = Pattern.compile("\\d{4}");

    public String getDescription(Document doc) {
        if (null == doc) {
            return null;
        }

        Elements description = doc.select("meta[name=description]");
        return null != description ? description.attr("content") : "";
    }

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

        Elements results = doc.select("table[class=findList]");
        if (null == results) {
            return res;
        }

        results = results.select("tr");
        if (null == results || results.isEmpty()) {
            return res;
        }

        results.stream().forEach(result -> {
            Element element = result.select("td[class=primary_photo]>a").first();
            String detailUrl = null, imageUrl = null, description = null;
            Integer year = null;
            if (null != element) {
                detailUrl = element.attributes().get("href");

                element = element.select("img").first();
                if (null != element) {
                    imageUrl = element.attributes().get("src");
                }
            }

            element = result.select("td[class=result_text]").first();
            if (null != element) {
                Element elementHref = element.select("a").first();

                if (null != elementHref) {
                    description = element.text();
                }

                String yearText = null;
                Matcher matcher = NUMBER.matcher(element.text().trim());
                if (matcher.find()) {
                    yearText = matcher.group(0);
                    year = Integer.valueOf(yearText);
                }
            }

            if (null != detailUrl && null != imageUrl && null != description && detailUrl.startsWith("/title")) {
                res.add(new UrlDTO("https://www.imdb.com" + detailUrl, description, imageUrl, year));
            }

        });

        return res;
    }

    public String getImageUrl(Document doc) {
        if (null == doc) {
            return null;
        }

        // <link rel='image_src' href="https://m.media-amazon.com/images/M/MV5BNzQzOTk3OTAtNDQ0Zi00ZTVkLWI0MTEtMDllZjNkYzNjNTc4L2ltYWdlXkEyXkFqcGdeQXVyNjU0OTQ0OTY@._V1_UY1200_CR84,0,630,1200_AL_.jpg">
        Element link = doc.select("link[rel=image_src]").first();
        if (null == link) {
            LOG.log(Level.SEVERE, "Cannot read link");
            return null;
        }

        return link.attr("href");
    }

    public Integer getYear(Document doc) {
        if (null == doc) {
            return null;
        }

        Element datePublished = doc.select("span[id=titleYear]>a").first();
        if (null == datePublished) {
            LOG.log(Level.SEVERE, "Cannot read origin");
            return null;
        }

        String dateText = datePublished.text();
        if (null == dateText || dateText.length() != 4) {
            LOG.log(Level.SEVERE, "Bad format of date text (1). Should be yyyy, is " + dateText, dateText);
            return null;
        }

        Integer year;

        try {
            year = Integer.valueOf(dateText);
        } catch (java.lang.NumberFormatException ex) {
            year = null;
        }


        return year;
    }
}
