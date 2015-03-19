package com.xinflood.domainobject;

import com.google.common.base.MoreObjects;

import java.util.UUID;

public class User {
    private final UUID id;
    private final String username;
    private final String password;
    private final String accessToken;
    private final LoginPlatform loginPlatform;

    public User(UUID id, String username, String password, String accessToken) {
        this(id, username, password, accessToken, LoginPlatform.NATIVE);
    }

    public User(UUID id, String username, String password, String accessToken, LoginPlatform loginPlatform) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.accessToken = accessToken;
        this.loginPlatform = loginPlatform;
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

    public LoginPlatform getLoginPlatform() {
        return loginPlatform;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("username", username)
                .add("password", password)
                .add("accessToken", accessToken)
                .add("loginPlatform", loginPlatform)
                .toString();
    }
}
