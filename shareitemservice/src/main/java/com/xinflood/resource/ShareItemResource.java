package com.xinflood.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by xinxinwang on 11/16/14.
 */
@Api(value = "/api/shareitem", description = "share a new item")
@Path("/api/shareitem")
@Produces(MediaType.APPLICATION_JSON)
public class ShareItemResource {

    private final ShareItemController shareItemController;

    public ShareItemResource(ShareItemController shareItemController, ObjectMapper objectMapper) {
        this.shareItemController = shareItemController;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("/{userId}")
    @ApiOperation(value = "add a new item under a given user", response = String.class)
    public Response addItem(
            @Auth User user,
            @ApiParam(required = true, value = "user id") @PathParam("userId") UUID userId,
            RequestItemMetadata requestItemMetadata
    ) throws IOException, ExecutionException, InterruptedException {
        checkState(user.getId().equals(userId), "unauthorized access for %s", userId);

        Item created = shareItemController.addNewItem(userId, requestItemMetadata);
        return Response.ok(ImmutableMap.of("item", created)).build();
    }


    @GET
    @ApiOperation(value = "get items for an optional user", response = String.class)
    public Response getItems(@QueryParam("size") @DefaultValue("10") int size,
                             @QueryParam("offset") Optional<Integer> offset,
                             @QueryParam("userId") UUID userId) throws ExecutionException, InterruptedException {

        List<Item> items = shareItemController.getItems(size, offset.or(0), Optional.fromNullable(userId));
        return Response.ok(ImmutableMap.of("items", items)).build();
    }

    @GET
    @ApiOperation(value = "get an item for the item id", response = String.class)
    @Path("/{itemId}")
    public Response getItem(@PathParam("itemId") UUID itemId) throws ExecutionException, InterruptedException {


        Optional<Item> item = shareItemController.getItem(itemId);
        return Response.ok(ImmutableMap.of("item", item)).build();
    }
}
