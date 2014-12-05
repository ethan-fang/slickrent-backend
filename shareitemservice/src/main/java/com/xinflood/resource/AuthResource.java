package com.xinflood.resource;


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.Responses;
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
public class AuthResource {
	private ImmutableList<String> allowedGrantTypes;
	private UserDao userDao;

	public AuthResource(List<String> allowedGrantTypes, UserDao userDao) {
		this.allowedGrantTypes = ImmutableList.copyOf(allowedGrantTypes);
		this.userDao = userDao;

	}

    @POST
    @Path("/oauth/token")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postForToken(
            @FormParam("grantType") String grantType,
            @FormParam("username") String username,
            @FormParam("password") String password,
            @FormParam("clientId") String clientId
    ) {
        // Check if the grant type is allowed
        if (!allowedGrantTypes.contains(grantType)) {
            Response response = Response.status(Responses.METHOD_NOT_ALLOWED).build();
            throw new WebApplicationException(response);
        }

        // Try to find a user with the supplied credentials.
        Optional<User> user = userDao.findUserByUsernameAndPassword(username, password);
        if (user == null || !user.isPresent()) {
            throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        return Response.ok(ImmutableMap.of("token", user.get().getAccessToken())).build();
    }

    @POST
    @Path("/signin")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
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
    public Response userSignUp(@FormParam("username") String username, @FormParam("password") String password) {
        Optional<User> user = userDao.createNewUser(username, password);

        return Response.ok(ImmutableMap.of("user", user)).build();
    }



}
