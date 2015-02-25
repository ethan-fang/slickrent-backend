package com.xinflood.resource;


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import com.xinflood.dao.UserDao;
import com.xinflood.domainobject.User;
import com.xinflood.domainobject.UsernamePasswordPair;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/user")
@Api(value = "/api/user", description = "user sign in/up")
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
    private UserDao userDao;

    public AuthResource(UserDao userDao) {
        this.userDao = userDao;
    }

    @POST
    @Path("/signin")
    @ApiOperation(value = "sign in with username and password to retrieve user token", response = String.class)
    public Response getToken(
            @ApiParam(value = "username and password in json", required = true)
            UsernamePasswordPair usernamePasswordPair
    ) {
        // Try to find a user with the supplied credentials.
        Optional<User> user = userDao.findUserByUsernameAndPassword(usernamePasswordPair.getUsername(), usernamePasswordPair.getPassword());
        if (user == null || !user.isPresent()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.UNAUTHORIZED)
                            .entity(ImmutableMap.of("error", String.format("user %s not found", usernamePasswordPair.getUsername())))
                            .build());
        }

        return Response.ok(ImmutableMap.of("user", user.get())).build();
    }


    // temporary comment out to disable signup
    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "new user sign up", response = String.class)
    public Response userSignUp(
            @ApiParam(value = "username and password in json", required = true)
            UsernamePasswordPair usernamePasswordPair
    ) {
        String username = usernamePasswordPair.getUsername();
        String password = usernamePasswordPair.getPassword();
        Optional<User> user = userDao.createNewUser(username, password);

        return Response.ok(ImmutableMap.of("user", user)).build();
    }
}
