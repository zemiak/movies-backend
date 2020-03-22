package com.zemiak.movies.language;

import javax.enterprise.context.ApplicationScoped;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class LanguageRepository implements PanacheRepository<Language> {
}
