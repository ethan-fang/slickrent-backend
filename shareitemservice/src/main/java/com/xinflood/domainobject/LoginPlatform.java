package com.xinflood.domainobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by xinxinwang on 3/1/15.
 */
public enum LoginPlatform {
    NATIVE("NATIVE"), FB("FB"), GOOGLE("GOOGLE");

    private final String name;

    @JsonCreator
    LoginPlatform(@JsonProperty("name") String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
