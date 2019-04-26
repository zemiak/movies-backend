package com.zemiak.movies.service.tvml;

import java.io.File;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Stateless
@Path("tvml")
@Produces(value = MediaType.APPLICATION_JSON)
@Consumes(value = MediaType.APPLICATION_JSON)
public class TvmlResource {
    @Inject
    TvmlReader reader;

    @Inject
    TvmlCovers covers;

    @Inject
    CacheDataReader cache;

    @GET
    public Response getData(@QueryParam("path") @DefaultValue("") String path) {
        TvmlData data = reader.readData(path);
        return Response.ok(data, MediaType.APPLICATION_JSON).build();
    }

    @PUT
    @Path("movies/{id}/played")
    public Response played(@NotNull @PathParam("id") Long id) {
        return Response.ok("Mock mock").build();
    }


    @GET
    @Path("covers")
    public Response getCover(@QueryParam("path") @DefaultValue("") String path) {
        File file = covers.getCoverFile(path);
        if (null == file) {
            return Response.status(Response.Status.GONE).build();
        }

        String fileName = covers.getFileName(path);
        Response.ResponseBuilder response = Response.ok((Object) file);
        response.header("Content-Disposition", "attachment; filename=" + fileName);
        response.header("Content-Type", "image/jpeg");
        return response.build();
    }

    @GET
    @Path("data")
    public Response getData() {
        return Response
                .ok(buildData())
                .build();
    }

    private JsonObject buildData() {
        JsonObject version = Json.createObjectBuilder()
                .add("version", cache.getVersion())
                .add("motd", "")
                .build();
        JsonObject dataCache = cache.getCache();
        JsonObject data = Json.createObjectBuilder()
                .add("version", version)
                .add("cache", dataCache)
                .build();

        return data;
    }
}
