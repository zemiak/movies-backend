package com.zemiak.movies.batch.infuse;

import com.zemiak.movies.domain.Movie;
import com.zemiak.movies.domain.Serie;
import com.zemiak.movies.strings.Encodings;
import java.util.Objects;
import javax.enterprise.context.Dependent;

@Dependent
public class InfuseSerieName {
    static final Integer GOT = 1000;
    static final Integer MASH = 1;

    public String process(Movie movie) {
        Serie serie = movie.getSerie();
        String name;

        if (Objects.equals(GOT, serie.getId())) {
            name = process(movie, 2, movie.getDisplayOrder() / 100);
        } else if (Objects.equals(MASH, serie.getId())) {
            name = process(movie, 3, 1);
        } else {
            name = process(movie, 2, 1);
        }

        return name;
    }

    private String process(Movie movie, Integer decimals, Integer season) {
        String serie = Encodings.deAccent(movie.getSerieName());
        String seasonNumber = String.format("%02d", season);
        String format = "%0" + String.valueOf(decimals) + "d";
        Integer number = null == movie.getDisplayOrder() ? 0 : movie.getDisplayOrder();
        String movieName = (null == movie.getOriginalName() || "".equals(movie.getOriginalName().trim()))
                ? movie.getName() : movie.getOriginalName();

        if (null == movieName || "".equals(movieName)) {
            movieName = "";
        } else {
            movieName = Encodings.deAccent(movieName);
        }

        String episodeName = serie + ".S" + seasonNumber + "E" + String.format(format, number)
                + "." + movieName + ".m4v";

        return episodeName;
    }
}