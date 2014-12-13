package com.xinflood.auth;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 */
public class ClientIdRequestFilter implements ContainerRequestFilter {
    private final ImmutableSet<UUID> validClientIds;
    private final ImmutableSet<Pattern> filterPathPatterns;

    public ClientIdRequestFilter(Set<UUID> validClientIds, Set<Pattern> filterPathPatterns)
    {
        this.filterPathPatterns = ImmutableSet.copyOf(checkNotNull(filterPathPatterns, "filterPathPatterns is null"));
        this.validClientIds = ImmutableSet.copyOf(checkNotNull(validClientIds, "validClientIds is null"));
    }

    @Override
    public ContainerRequest filter(ContainerRequest request)
    {
        for(Pattern pattern : filterPathPatterns) {
            Matcher matcher = pattern.matcher(request.getPath());
            if(matcher.find()) {
                String clientId = request.getQueryParameters().getFirst("clientId");
                Response.ResponseBuilder responseBuilder = Response.status(Response.Status.UNAUTHORIZED)
                        .header("Content-Type", "application/json");

                if(clientId ==null || !javaUuidConvertable(clientId)) {
                    responseBuilder.entity(ImmutableMap.of("error", "please provide a valid clientId"));
                    throw new WebApplicationException(responseBuilder.build());
                }
                else if(!validClientIds.contains(UUID.fromString(clientId))) {
                    responseBuilder.entity(ImmutableMap.of("error", "clientId is not authorized"));
                    throw new WebApplicationException(responseBuilder.build());
                }
            }
        }
        return request;
    }

    private boolean javaUuidConvertable(String uuid) {
        try {
            UUID.fromString(uuid);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
