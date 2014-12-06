package com.xinflood;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sun.jersey.multipart.impl.MultiPartConfigProvider;
import com.xinflood.auth.BearerTokenOAuth2Provider;
import com.xinflood.config.ShareItemServerConfiguration;
import com.xinflood.dao.PostgresDao;
import com.xinflood.dao.S3ImageDao;
import com.xinflood.resource.AuthResource;
import com.xinflood.resource.ShareItemResource;
import io.dropwizard.Application;
import io.dropwizard.auth.oauth.OAuthProvider;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.lifecycle.ExecutorServiceManager;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.skife.jdbi.v2.DBI;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by xinxinwang on 11/16/14.
 */
public class ShareItemServerMain extends Application<ShareItemServerConfiguration> {

    public static void main(String[] args) throws Exception {
        new ShareItemServerMain().run(args);
    }

    @Override
    public void initialize(Bootstrap<ShareItemServerConfiguration> bootstrap) {
    }

    @Override
    public void run(ShareItemServerConfiguration config, Environment environment) throws Exception {

        final ObjectMapper objectMapper = environment.getObjectMapper();
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new ISO8601DateFormat());


        final AmazonS3Client s3Client = new AmazonS3Client(
                new BasicAWSCredentials(config.getAwsAccessKeyId(), config.getAwsSecretAccesskey()));


        final DBIFactory factory = new DBIFactory();
        final DBI dbi = factory.build(environment, config.getDatabase(), "proximity");

        final PostgresDao postgresDao = new PostgresDao(dbi);
        final ShareItemController shareItemController = new ShareItemController(
                config, new S3ImageDao(s3Client, config.getS3BucketName()), postgresDao,
                getManagedListeningExecutorService(environment, 100, "item process pool")
        );
        final ShareItemResource shareItemResource = new ShareItemResource(shareItemController, environment.getObjectMapper());
        final AuthResource authResource = new AuthResource(config.getAuthConfiguration().getAllowedGrantTypes(), postgresDao);


        environment.jersey().register(shareItemResource);
        environment.jersey().register(authResource);

        environment.jersey().register(MultiPartConfigProvider.class);
        environment.jersey().register(com.sun.jersey.multipart.impl.MultiPartReaderServerSide.class);

        BearerTokenOAuth2Provider oAuth2Provider = new BearerTokenOAuth2Provider(postgresDao);
        environment.jersey().register(new OAuthProvider<>(oAuth2Provider, "app_realm"));

        environment.healthChecks().register("heartbeat", new HeartbeatHealthCheck());

    }

    private ListeningExecutorService getManagedListeningExecutorService(final Environment environment,
                                                                        final int maxPoolSize,
                                                                        final String name)
    {


        ExecutorService executorService = getListeningExecutorService(maxPoolSize, name);
        final ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(executorService);

        environment.lifecycle().manage(new ExecutorServiceManager(listeningExecutorService,
                io.dropwizard.util.Duration.seconds(5), name));
        return listeningExecutorService;
    }

    private ExecutorService getListeningExecutorService(final int maxPoolSize, final String name)
    {
        return new ThreadPoolExecutor(maxPoolSize, maxPoolSize, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(maxPoolSize),
                new ThreadFactoryBuilder().setDaemon(true).setNameFormat(name + "-%s").build(),
                new ThreadPoolExecutor.AbortPolicy());
    }
}
