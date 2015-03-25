package com.xinflood.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by xinxinwang on 3/24/15.
 */
public class PasswordPair {
    private final String oldPassword;
    private final String newPassword;


    @JsonCreator
    public PasswordPair(
            @JsonProperty("oldPassword") String oldPassword,
            @JsonProperty("newPassword") String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
