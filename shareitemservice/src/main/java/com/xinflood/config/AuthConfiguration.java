package com.xinflood.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

public class AuthConfiguration extends Configuration {
	@Valid
	@JsonProperty
	private ImmutableList<String> allowedGrantTypes = ImmutableList.of("password");

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

    public void setAllowedGrantTypes(ImmutableList<String> allowedGrantTypes) {
        this.allowedGrantTypes = allowedGrantTypes;


    }
}
