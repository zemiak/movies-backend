package com.zemiak.movies.serie;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class SerieRepository implements PanacheRepository<Serie> {
}
