package com.zemiak.movies.genre;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class GenreRepository implements PanacheRepository<Genre> {
}
