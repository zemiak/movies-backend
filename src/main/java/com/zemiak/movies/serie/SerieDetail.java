package com.zemiak.movies.serie;

import java.util.HashMap;
import java.util.Map;

import com.zemiak.movies.genre.Genre;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;

public class SerieDetail extends SerieUI  {
    public Long genreId;
    public Map<Long, String> genres;
    public Boolean tvShow;

    public static SerieDetail forNew() {
        SerieDetail dto = new SerieDetail();

        dto.genres = new HashMap<>();
        Genre.streamAll(Sort.by("displayOrder"))
            .map(genreBase -> {return (Genre) genreBase;})
            .forEach(genre -> {
                dto.genres.put(genre.id, genre.name);
            });

        return dto;
    }

	public static SerieDetail of(PanacheEntity base) {
        SerieDetail dto = forNew();
        SerieUI.copy(dto, base);

        Serie entity = (Serie) base;
        dto.genreId = entity.genreId;
        dto.tvShow = entity.tvShow;

        return dto;
	}

}
