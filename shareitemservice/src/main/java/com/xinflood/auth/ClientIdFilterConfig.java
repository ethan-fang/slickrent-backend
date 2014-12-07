package com.xinflood.auth;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

import javax.annotation.Nullable;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Set;
import java.util.UUID;

/**
 */
public class ClientIdFilterConfig implements FilterConfig {


    private final String clientIdsSerialized;

    public ClientIdFilterConfig(Set<UUID> clientIds) {
        clientIdsSerialized = Joiner.on(",").join(Collections2.transform(
            clientIds, new Function<UUID, String>() {
                    @Nullable
                    @Override
                    public String apply(UUID input) {
                        return input.toString();
                    }
                }
        ));
    }

    @Override
    public String getFilterName() {
        return "client id filter";
    }

    @Override
    public ServletContext getServletContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getInitParameter(String name) {
        if(name.equals("clientId")) {
            return clientIdsSerialized;
        }
        return null;
    }

    @Override
    public Enumeration<String> getInitParameterNames() {
        throw new UnsupportedOperationException();
    }
}
