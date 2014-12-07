package com.xinflood.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class AuthConfiguration extends Configuration {
	@Valid
	@JsonProperty
	private ImmutableList<String> allowedGrantTypes = ImmutableList.of("password");

    private ImmutableSet<UUID> allowedClientIds = ImmutableSet.of(UUID.fromString("e7568b2c-2c0f-480e-9e34-08f9a4b807dc"));

	@Valid
	@JsonProperty
	@NotEmpty
	private String bearerRealm = "share";

    @Valid
    @NotNull
    @JsonProperty
    private DataSourceFactory database = new DataSourceFactory();

    public DataSourceFactory getDatabase() {
        return database;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public List<String> getAllowedGrantTypes() {
        return allowedGrantTypes;
    }

    public String getBearerRealm() {
        return bearerRealm;
    }

    public ImmutableSet<UUID> getAllowedClientIds() {
        return allowedClientIds;
    }

    public void setAllowedClientIds(ImmutableSet<UUID> allowedClientIds) {
        this.allowedClientIds = allowedClientIds;
    }

    public void setAllowedGrantTypes(ImmutableList<String> allowedGrantTypes) {
        this.allowedGrantTypes = allowedGrantTypes;


    }
}
