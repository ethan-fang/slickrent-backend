package com.xinflood.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Created by xinxinwang on 11/16/14.
 */
public class ShareItemServerConfiguration extends Configuration implements WithAuthConfiguration {

    private String s3BucketName = "share-images-xinflood";
    private String awsAccessKeyId="";
    private String awsSecretAccesskey="";

    private AuthConfiguration authConfiguration = new AuthConfiguration();


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
    public AuthConfiguration getAuthConfiguration() {
        return authConfiguration;
    }
}
