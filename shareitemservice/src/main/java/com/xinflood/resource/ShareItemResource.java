package com.xinflood.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.xinflood.ShareItemController;
import com.xinflood.domainobject.Item;
import com.xinflood.domainobject.RequestItemMetadata;
import com.xinflood.domainobject.User;
import io.dropwizard.auth.Auth;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * Created by xinxinwang on 11/16/14.
 */
@Api(value = "/shareitem", description = "share a new item")
@Path("/shareitem")
@Produces(MediaType.APPLICATION_JSON)
public class ShareItemResource {

    private final ShareItemController shareItemController;
    private final ObjectMapper objectMapper;

    public ShareItemResource(ShareItemController shareItemController, ObjectMapper objectMapper) {
        this.shareItemController = shareItemController;
        this.objectMapper = objectMapper;
    }

    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/{userId}")
    @ApiOperation(value = "add a new item under a given user", response = String.class)
    public Response addItem(@Auth User user, @ApiParam(required = true, value = "user id") @PathParam("userId") UUID userId,
            FormDataMultiPart formDataMultiPart
    ) throws IOException {
        checkState(user.getId().equals(userId), "unauthorized access for %s", userId);

        checkNotNull(formDataMultiPart.getFields("file"));
        checkNotNull(formDataMultiPart.getFields("metadata"));

        Collection<InputStream> images = Collections2.transform(formDataMultiPart.getFields("file"), new Function<FormDataBodyPart, InputStream>() {
            @Override
            public InputStream apply(FormDataBodyPart input) {
                return input.getValueAs(InputStream.class);
            }
        });

        String metadata = formDataMultiPart.getField("metadata").getValueAs(String.class);
        RequestItemMetadata requestItemMetadata = objectMapper.readValue(metadata, RequestItemMetadata.class);

        Item created = shareItemController.addNewItem(userId, requestItemMetadata, images);
        return Response.ok(ImmutableMap.of("item", created)).build();
    }


    @GET
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @ApiOperation(value = "get items for an optional user", response = String.class)
    public Response getItems(@QueryParam("size") @DefaultValue("10") int size, @QueryParam("userId") UUID userId) throws ExecutionException, InterruptedException {
        List<Item> items = shareItemController.getItems(size, Optional.fromNullable(userId));
        return Response.ok(ImmutableMap.of("items", items)).build();
    }
}
