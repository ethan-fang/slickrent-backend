package com.xinflood.resource;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.ByteStreams;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.xinflood.dao.ImageDao;
import com.xinflood.domainobject.User;
import io.dropwizard.auth.Auth;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 */
@Path("/image")
@Api(value = "/image", description = "image related endpoints")
@Produces(MediaType.APPLICATION_JSON)
public class ImageResource {
    private final ImageDao imageDao;

    public ImageResource(ImageDao imageDao) {
        this.imageDao = checkNotNull(imageDao);
    }

    @POST
    @ApiOperation(value = "upload new images", response = String.class)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(
            @Auth User user,
            FormDataMultiPart body) throws IOException {

        checkNotNull(body.getField("image"));

        InputStream image = body.getField("image").getValueAs(InputStream.class);
        UUID imageKey = UUID.randomUUID();

        if(imageDao.putImage(imageKey.toString(), ByteStreams.toByteArray(image))) {
            Response.Status status = Response.Status.CREATED;
            return Response.status(status).entity(
                    ImmutableMap.of("imageId", imageKey.toString(), "status", status.getStatusCode())).build();
        } else {
            throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ImmutableMap.of("error", "server error. please retry")).build());
        }
    }

    @GET
    @Path("/{imageId}")
    @Produces({"image/jpeg"})
    @ApiOperation(value = "download image", response = byte[].class)
    public Response getImage(@PathParam("imageId") UUID imageId) throws IOException {
        byte[] image = imageDao.getImage(imageId.toString());
        Response response = Response.ok(image).header("Content-Type", "image/jpeg").build();
        return response;
    }
}
