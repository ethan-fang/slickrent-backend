package com.xinflood.domainobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by xinxinwang on 12/18/14.
 */
public class Category {
    private final String name;
    private final String description;

    @JsonCreator
    public Category(@JsonProperty("name") String name, @JsonProperty("description") String description) {
        this.name = checkNotNull(name);
        this.description = checkNotNull(description);
    }

    @JsonProperty
    public String getName() {
        return name;
    }

    @JsonProperty
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("description", description)
                .toString();
    }
}
