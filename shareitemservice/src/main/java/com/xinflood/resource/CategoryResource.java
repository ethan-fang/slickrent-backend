package com.xinflood.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by xinxinwang on 12/18/14.
 */
@Path("/api/category")
@Api(value = "/api/category", description = "get category information")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {

    private final JsonNode categoryHierarchy;
    public CategoryResource(JsonNode categoryHierarchy) throws IOException {
        this.categoryHierarchy = checkNotNull(categoryHierarchy);
    }

    @GET
    @ApiOperation(value = "get all category hierarchies", response = String.class)
    public Response getCategoryHierarchy() {
        return Response.ok(categoryHierarchy).build();
    }
}
