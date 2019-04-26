package com.zemiak.movies.service.scraper;

import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.domain.UrlDTO;
import java.util.List;

public interface IWebMetadataReader {
    public boolean accepts(final Movie movie);
    public List<UrlDTO> getUrlCandidates(final String movieName);
    public String getReaderName();

    public String parseDescription(final Movie movie);

    public void processThumbnail(final Movie movie);
    public void setImageFileName(final String imageFileName);

    public Integer parseYear(final Movie movie);

    public String getWebPage(Movie movie);
}
