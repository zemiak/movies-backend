package com.zemiak.movies.health;

import javax.enterprise.context.ApplicationScoped;

import com.zemiak.movies.movie.Movie;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@Readiness
@ApplicationScoped
public class ReadinessCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        try {
            Long count = Movie.count();
            return HealthCheckResponse.up("movies-backend: count is " + count);
        } catch (Exception ex) {
            return HealthCheckResponse.down("movies-backend");
        }
    }
}
