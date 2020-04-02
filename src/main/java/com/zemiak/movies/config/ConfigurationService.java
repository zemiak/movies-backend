package com.zemiak.movies.config;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.zemiak.movies.strings.NullAwareJsonObjectBuilder;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@RequestScoped
@Path("/config")
@Produces(MediaType.APPLICATION_JSON)
public class ConfigurationService {
    @Inject
    @ConfigProperty(name = "quarkus.http.port")
    Integer port;

    @GET
    public JsonObject getConfig() {
        return NullAwareJsonObjectBuilder.create()
            .add("port", port)
            .build();
    }
}
