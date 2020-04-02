package com.zemiak.movies.infuse;

import java.util.Objects;

import javax.enterprise.context.Dependent;

import com.zemiak.movies.movie.Movie;
import com.zemiak.movies.strings.Encodings;

@Dependent
public class InfuseSerieName {
    static final Long GOT = 1000l;
    static final Long MASH = 1l;

    public String process(Movie movie) {
        String name;
        if (Objects.equals(GOT, movie.serieId)) {
            name = process(movie, 2, movie.displayOrder / 100);
        } else if (Objects.equals(MASH, movie.serieId)) {
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
        Integer number = null == movie.displayOrder ? 0 : movie.displayOrder;
        String movieName = (null == movie.originalName || "".equals(movie.originalName.trim()))
                ? movie.name : movie.originalName;

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
