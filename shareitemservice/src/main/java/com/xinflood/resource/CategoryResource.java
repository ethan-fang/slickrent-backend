package com.xinflood.resource;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created by xinxinwang on 12/18/14.
 */
@Path("/category")
@Api(value = "/category", description = "get category information")
@Produces(MediaType.APPLICATION_JSON)
public class CategoryResource {

    JsonNode categoryHierarchy;
    public CategoryResource(String categoryFilePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        categoryHierarchy = mapper.readValue(this.getClass().getResourceAsStream(categoryFilePath), JsonNode.class);
    }

    @GET
    @ApiOperation(value = "get all category hierarchies", response = String.class)
    public Response getCategoryHierarchy() {
        return Response.ok(categoryHierarchy).build();

    }
}
