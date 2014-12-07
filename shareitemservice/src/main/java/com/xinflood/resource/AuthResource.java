package com.xinflood.resource;


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.xinflood.dao.UserDao;
import com.xinflood.domainobject.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/user")
@Api(value = "/user", description = "user sign in/up")
public class AuthResource {
	private UserDao userDao;

	public AuthResource(List<String> allowedGrantTypes, UserDao userDao) {
		this.userDao = userDao;
	}

    @POST
    @Path("/signin")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "sign in with username and password to retrieve user token", response = String.class)
    public Response postForToken(
            @FormParam("username") String username,
            @FormParam("password") String password
    ) {
        // Try to find a user with the supplied credentials.
        Optional<User> user = userDao.findUserByUsernameAndPassword(username, password);
        if (user == null || !user.isPresent()) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        return Response.ok(ImmutableMap.of("token", user.get().getAccessToken())).build();
    }


    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "new user sign up", response = String.class)
    public Response userSignUp(@FormParam("username") String username, @FormParam("password") String password) {
        Optional<User> user = userDao.createNewUser(username, password);

        return Response.ok(ImmutableMap.of("user", user)).build();
    }



}
