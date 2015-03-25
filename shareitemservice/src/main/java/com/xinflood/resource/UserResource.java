package com.xinflood.resource;


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.xinflood.UserController;
import com.xinflood.domainobject.SocialSignInRequest;
import com.xinflood.domainobject.User;
import com.xinflood.domainobject.UserProfile;
import com.xinflood.domainobject.UsernamePasswordPair;
import com.xinflood.request.PasswordPair;
import io.dropwizard.auth.Auth;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Path("/api/user")
@Api(value = "/api/user", description = "user sign in/up, profile and other related endpoints")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private UserController userController;

    public UserResource(UserController userController) {
        this.userController = checkNotNull(userController);
    }

    @POST
    @Path("/signin")
    @ApiOperation(value = "sign in with username and password to retrieve user token", response = String.class)
    public Response getToken(
            @ApiParam(value = "username and password in json", required = true)
            UsernamePasswordPair usernamePasswordPair
    ) throws ExecutionException, InterruptedException {

        // Try to find a user with the supplied credentials.
        Optional<User> user = userController.findUserByUsernameAndPassword(usernamePasswordPair.getUsername(), usernamePasswordPair.getPassword());
        if (user == null || !user.isPresent()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity(ImmutableMap.of("error", String.format("user %s not found", usernamePasswordPair.getUsername())))
                            .build());
        }

        return Response.ok(ImmutableMap.of("user", user.get())).build();
    }

    @POST
    @Path("/social-login")
    @ApiOperation(value = "sign in with social network", response = String.class)
    public Response updateSocialToken(
            @ApiParam(value = "username and password in json", required = true)
            SocialSignInRequest socialSignInRequest
    ) throws ExecutionException, InterruptedException {

        // Try to find a user with the supplied credentials.
        Optional<User> user = userController.updateSocialLogin(socialSignInRequest);

        if(user.isPresent()) {
            return Response.ok(ImmutableMap.of("user", user.get())).build();
        } else {
            throw new WebApplicationException(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity(ImmutableMap.of("error", "user not found for request " + socialSignInRequest))
                            .build());
        }
    }


    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "new user sign up", response = String.class)
    public Response userSignUp(
            @ApiParam(value = "username and password in json", required = true)
            UsernamePasswordPair usernamePasswordPair
    ) throws ExecutionException, InterruptedException {
        String username = usernamePasswordPair.getUsername();
        String password = usernamePasswordPair.getPassword();
        Optional<User> user = userController.createNewUser(username, password);

        if(user.isPresent()) {
            return Response.ok(ImmutableMap.of("user", user)).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ImmutableMap.of("error", "user " + username + " already existed"))
                    .build();
        }
    }

    @PUT
    @Path("/password/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "new user sign up", response = String.class)
    public Response updatePassword(
            @Auth User user,
            @ApiParam(required = true, value = "user id") @PathParam("userId") UUID userId,
            PasswordPair passwordPair

    ) throws ExecutionException, InterruptedException {

        Optional<UUID> updatedUserId = userController.updatePassword(userId, passwordPair.getOldPassword(), passwordPair.getNewPassword());

        if(updatedUserId.isPresent()) {
            return Response.ok(ImmutableMap.of("userId", updatedUserId.get())).build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ImmutableMap.of("error", "username and password pair doesn't match the existing one"))
                    .build();
        }
    }




    @GET
    @Path("/profile/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUserProfile(
        @Auth User user,
        @ApiParam(required = true, value = "user id") @PathParam("userId") UUID userId
    ) throws ExecutionException, InterruptedException {

        checkState(user.getId().equals(userId), "unauthorized access for %s", userId);
        Optional<UserProfile> userProfile = userController.getUserProfileByUserId(userId);

        if(userProfile.isPresent()) {
            return Response.ok(ImmutableMap.of("profile", userProfile)).build();
        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(ImmutableMap.of("error", "user profile not found for id " + userId)).build();
        }
    }

    @PUT
    @Path("/profile/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUserProfile(
            @Auth User user,
            @ApiParam(required = true, value = "user id") @PathParam("userId") UUID userId,
            UserProfile userProfile
    ) throws ExecutionException, InterruptedException {
        checkState(user.getId().equals(userId), "unauthorized access for %s", userId);

        UUID userProfileId = userController.createOrUpdateUserProfile(userProfile, userId);
        return Response.ok(ImmutableMap.of("userProfileId", userProfileId, "userProfile", userProfile)).build();
    }

}
