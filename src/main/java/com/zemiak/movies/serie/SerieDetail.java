package com.zemiak.movies.serie;

import java.util.HashMap;
import java.util.Map;

import com.zemiak.movies.genre.Genre;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;

public class SerieDetail extends SerieUI  {
    public Long genreId;
    public Map<Long, String> genres;

	public static SerieDetail of(PanacheEntityBase base) {
        SerieDetail dto = new SerieDetail();
        SerieUI.copy(dto, base);

        Serie entity = (Serie) base;
        dto.genreId = entity.genreId;

        dto.genres = new HashMap<>();
        Genre.streamAll(Sort.by("displayOrder"))
            .map(genreBase -> {return (Genre) genreBase;})
            .forEach(genre -> {
                dto.genres.put(genre.id, genre.name);
            });

        return dto;
	}

}
