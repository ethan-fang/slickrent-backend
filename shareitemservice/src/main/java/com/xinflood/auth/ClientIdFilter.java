package com.xinflood.auth;

import com.google.common.collect.ImmutableSet;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by xinxinwang on 12/6/14.
 */
public class ClientIdFilter implements Filter {
    private ImmutableSet<UUID> clientIds;

    public ClientIdFilter(Set<UUID> clientIds) {
        this.clientIds = ImmutableSet.copyOf(clientIds);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if(request.getParameter("clientId") == null || !clientIds.contains(UUID.fromString(request.getParameter("clientId")))) {
            HttpServletResponse.class.cast(response).sendError(Response.Status.UNAUTHORIZED.getStatusCode(), "clientId is not authorized");
            return;
        }

        chain.doFilter(request, response);

    }

    @Override
    public void destroy() {

    }
}
