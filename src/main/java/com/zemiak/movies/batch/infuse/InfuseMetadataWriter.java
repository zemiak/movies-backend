package com.zemiak.movies.batch.infuse;

import com.zemiak.movies.domain.Movie;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import javax.enterprise.context.Dependent;

@Dependent
public class InfuseMetadataWriter {
    private static final String TEMPLATE = "<media type=\"Movie\">\n"
            + "<title>{{title}}</title>\n"
            + "<description>{{description}}</description>\n"
            + "<rating>R</rating>\n"
            + "<userStarRating>1</userStarRating>\n"
            + "<published>{{published}}</published>\n"
            + "<genres><genre>{{genre}}</genre></genres>\n"
            + "<cast></cast>\n"
            + "<producers></producers>\n"
            + "<directors></directors>\n"
            + "</media>\n";

    public void createMetadataFile(Movie movie, String movieName, Path linkName) throws IOException {
        String linkAbsoluteName = linkName.toString();
        int pos = linkAbsoluteName.lastIndexOf("/");
        String fileNameWithExt = linkAbsoluteName.substring(pos + 1);
        String filePath = linkAbsoluteName.substring(0, pos);
        pos = fileNameWithExt.lastIndexOf(".");
        String fileNameWithoutExt = fileNameWithExt.substring(0, pos);

        Path metadataFile = Paths.get(filePath, fileNameWithoutExt + ".xml");
        List<String> lines = Arrays.asList(getMetadataString(movie, movieName).split("\n"));

        Files.write(metadataFile, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE_NEW);
    }

    private String getMetadataString(Movie movie, String movieName) {
        String published = null == movie.getYear() ? "" : movie.getYear() + "-01-01";
        String description = null == movie.getDescription() ? "" : movie.getDescription();

        return TEMPLATE
                .replace("{{title}}", movieName)
                .replace("{{description}}", description)
                .replace("{{published}}", published)
                .replace("{{genre}}", movie.getGenreName());
    }


}
