package com.zemiak.movies.movie;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.json.JsonArray;
import javax.json.JsonValue;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Transactional
public class MovieBatchService {
    @Path("filternew")
    @POST
    public List<String> searchByFilename(List<String> data) {
        return data.stream().filter(fileName -> null == Movie.find("fileName", fileName).firstResult()).collect(Collectors.toList());
    }
}
