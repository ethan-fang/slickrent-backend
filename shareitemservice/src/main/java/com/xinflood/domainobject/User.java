package com.xinflood.domainobject;

import com.google.common.base.MoreObjects;

import java.util.UUID;

public class User {
    private final UUID id;
    private final String username;
    private final String password;

    private final String accessToken;

    public User(UUID id, String username, String password, String accessToken) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.accessToken = accessToken;
    }


    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getAccessToken() {
        return accessToken;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("username", username)
                .add("password", password)
                .add("accessToken", accessToken)
                .toString();
    }
}
