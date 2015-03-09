package com.xinflood.domainobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by xinxinwang on 3/1/15.
 */
public class SocialSignInRequest {
    private final String username;
    private final String token;
    private final LoginPlatform loginPlatform;

    @JsonCreator
    public SocialSignInRequest(@JsonProperty("username") String username,
                               @JsonProperty("token") String token,
                               @JsonProperty("loginPlatform") Optional<LoginPlatform> loginPlatform
    ) {
        this.username = checkNotNull(username);
        this.token = checkNotNull(token);
        checkNotNull(loginPlatform);
        this.loginPlatform = loginPlatform.isPresent()?loginPlatform.get() : LoginPlatform.NATIVE;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public LoginPlatform getLoginPlatform() {
        return loginPlatform;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("username", username)
                .add("token", token)
                .add("socialPlatform", loginPlatform)
                .toString();
    }



}
