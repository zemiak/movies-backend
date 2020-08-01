package com.zemiak.movies.csfd;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;

import javax.enterprise.context.Dependent;

import com.zemiak.movies.batch.logs.BatchLogger;
import com.zemiak.movies.imdb.Imdb;
import com.zemiak.movies.scraper.JsoupUtils;
import com.zemiak.movies.scraper.UrlDTO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

@Dependent
public class Csfd {
    private static BatchLogger LOG = BatchLogger.getLogger(Csfd.class.getName());

    private static String SEARCH_URL = "https://www.csfd.cz/hledat/?q=";

    public String parseDescription(String webPage) {
        Document doc = Jsoup.parse(webPage);
        if (null == doc) {
            return null;
        }

        Elements description = doc.select("div[data-truncate]");
        return null != description ? description.text() : "";
    }

    public List<UrlDTO> getUrlCandidates(String movieName) {
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
            Element hrefElement = li.select("div > h3 > a").first();
            if (null == hrefElement) {
                return;
            }

            Element imageElement = li.select("a > img").first();
            if (null == imageElement) {
                return;
            }
            String imageUrl = "https:" + imageElement.attributes().get("src");

            Element yearParagraph = li.select("div > p").first();
            Integer year = null;
            if (null != yearParagraph) {
                Matcher matcher = Imdb.NUMBER.matcher(yearParagraph.text().trim());
                if (matcher.find()) {
                    year = Integer.valueOf(matcher.group(0));
                }
            }

            String description = hrefElement.text();
            String detailUrl = hrefElement.absUrl("href");

            if (null != detailUrl && null != imageUrl && null != description) {
                res.add(new UrlDTO(detailUrl, description += " (" + year + ")", imageUrl, year));
            }
        });

        return res;
    }

    public String getImageUrl(Document doc) {
        if (null == doc) {
            return null;
        }

        Element poster = doc.select("img[class=film-poster]").first();
        if (null == poster) {
            LOG.log(Level.SEVERE, "Cannot read poster", null);
            return null;
        }

        String imageUrl = poster.attr("src");
        if (! imageUrl.startsWith("http")) {
            imageUrl = "http:" + imageUrl;
        }

        if (imageUrl.contains("?")) {
            int pos = imageUrl.indexOf('?');
            imageUrl = imageUrl.substring(0, pos);
        }

        return imageUrl;
    }

    public Integer getYear(Document doc) {
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
}
