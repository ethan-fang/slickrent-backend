package com.xinflood.migration;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;


/**
 * Created by xinxinwang on 1/17/15.
 */
public class DbMigrationConfiguration {
    @NotNull
    @JsonProperty
    private String dataSource = null;

    @JsonProperty
    private String targetVersion = null;

    @JsonProperty
    private String location = null;

    @JsonProperty
    private String user = null;

    @JsonProperty
    private String password = null;

    @NotNull
    @JsonProperty
    private String schema = null;

    @JsonProperty
    private boolean isEnabled = false;

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getTargetVersion() {
        return targetVersion;
    }

    public void setTargetVersion(String targetVersion) {
        this.targetVersion = targetVersion;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
