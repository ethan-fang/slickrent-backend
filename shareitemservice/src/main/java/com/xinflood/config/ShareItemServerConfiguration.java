package com.xinflood.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.xinflood.migration.DbMigrationConfiguration;
import com.xinflood.migration.WithDbMigrationConfiguration;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;

/**
 * Created by xinxinwang on 11/16/14.
 */
public class ShareItemServerConfiguration extends Configuration implements WithClientIdConfiguration, WithAuthConfiguration, WithDbMigrationConfiguration {

    private String s3BucketName = "share-images-xinflood";
    private String awsAccessKeyId="";
    private String awsSecretAccesskey="";
    private URI hostBaseUri = URI.create("ec2-54-173-114-114.compute-1.amazonaws.com");

    private ClientIdConfiguration clientIdConfiguration = new ClientIdConfiguration();
    private AuthConfiguration authConfiguration = new AuthConfiguration();
    private DbMigrationConfiguration dbMigrationConfiguration = new DbMigrationConfiguration();


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

    public String getS3BucketName() {
        return s3BucketName;
    }

    @JsonProperty
    public void setS3BucketName(String s3BucketName) {
        this.s3BucketName = s3BucketName;
    }


    public String getAwsAccessKeyId() {
        return awsAccessKeyId;
    }

    public void setAwsAccessKeyId(String awsAccessKeyId) {
        this.awsAccessKeyId = awsAccessKeyId;
    }

    public String getAwsSecretAccesskey() {
        return awsSecretAccesskey;
    }

    public void setAwsSecretAccesskey(String awsSecretAccesskey) {
        this.awsSecretAccesskey = awsSecretAccesskey;
    }

    @Override
    public ClientIdConfiguration getClientIdConfiguration() {
        return clientIdConfiguration;
    }

    @Override
    public AuthConfiguration getAuthConfiguration() {
        return authConfiguration;
    }

    public URI getHostBaseUri() {
        return hostBaseUri;
    }

    public void setHostBaseUri(URI hostBaseUri) {
        this.hostBaseUri = hostBaseUri;
    }

    public void setClientIdConfiguration(ClientIdConfiguration clientIdConfiguration) {
        this.clientIdConfiguration = clientIdConfiguration;
    }

    public void setAuthConfiguration(AuthConfiguration authConfiguration) {
        this.authConfiguration = authConfiguration;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("s3BucketName", s3BucketName)
                .add("awsAccessKeyId", awsAccessKeyId)
                .add("awsSecretAccesskey", awsSecretAccesskey)
                .add("hostBaseUri", hostBaseUri)
                .add("clientIdConfiguration", clientIdConfiguration)
                .add("authConfiguration", authConfiguration)
                .add("database", database)
                .toString();
    }

    @NotNull
    @Override
    public DbMigrationConfiguration getDbMigrationConfiguration() {
        return dbMigrationConfiguration;
    }

    @JsonProperty
    public ShareItemServerConfiguration setDbMigrationConfiguration(DbMigrationConfiguration dbMigrationConfiguration) {
        this.dbMigrationConfiguration = dbMigrationConfiguration;
        return this;
    }
}
