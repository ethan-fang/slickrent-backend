package com.xinflood.domainobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 */
public class UsernamePasswordPair {
    private final String username;
    private final String password;

    @JsonCreator
    public UsernamePasswordPair(@JsonProperty("username") String username,
                                @JsonProperty("password") String password) {
        this.username = checkNotNull(username);
        this.password = checkNotNull(password);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("username", username)
                .add("password", password)
                .toString();
    }
}
