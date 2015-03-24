package com.xinflood;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sun.jersey.api.container.filter.LoggingFilter;
import com.sun.jersey.multipart.impl.MultiPartConfigProvider;
import com.xinflood.auth.BearerTokenOAuth2Provider;
import com.xinflood.auth.ClientIdRequestFilter;
import com.xinflood.config.ClientIdConfiguration;
import com.xinflood.config.ShareItemServerConfiguration;
import com.xinflood.dao.ImageDao;
import com.xinflood.dao.PostgresDao;
import com.xinflood.dao.S3ImageDao;
import com.xinflood.migration.DbMigrationBundle;
import com.xinflood.migration.WithDbMigrationConfiguration;
import com.xinflood.misc.autozone.AutoZoneDataLoaderCommand;
import com.xinflood.resource.UserResource;
import com.xinflood.resource.CategoryResource;
import com.xinflood.resource.ImageResource;
import com.xinflood.resource.ShareItemResource;
import io.dropwizard.Application;
import io.dropwizard.auth.oauth.OAuthProvider;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.lifecycle.ExecutorServiceManager;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerDropwizard;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 */
public class ShareItemServerMain extends Application<ShareItemServerConfiguration> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShareItemServerMain.class);
    private final SwaggerDropwizard swaggerDropwizard = new SwaggerDropwizard();

    public static void main(String[] args) throws Exception {
        new ShareItemServerMain().run(args);
    }

    @Override
    public void initialize(Bootstrap<ShareItemServerConfiguration> bootstrap) {
        swaggerDropwizard.onInitialize(bootstrap);

        // add flyway migration
        bootstrap.addBundle(new DbMigrationBundle<WithDbMigrationConfiguration>());

        bootstrap.addCommand(new AutoZoneDataLoaderCommand(this, "autozoneload", "autozone-data-loader"));
    }

    @Override
    public void run(ShareItemServerConfiguration config, Environment environment) throws Exception {
        LOGGER.info(config.toString());

        swaggerDropwizard.onRun(config, environment, config.getHostBaseUri().toString());

        //allow CORS for localhost access
        configureCors(environment);

        final ObjectMapper objectMapper = environment.getObjectMapper();
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new ISO8601DateFormat());


        final AmazonS3Client s3Client = new AmazonS3Client(
                new BasicAWSCredentials(config.getAwsAccessKeyId(), config.getAwsSecretAccesskey()));


        final DBIFactory factory = new DBIFactory();
        final DBI dbi = factory.build(environment, config.getDatabase(), "proximity");

        ExecutorService executorService = getManagedListeningExecutorService(environment, 100, "item process pool");

        final ImageDao imageDao = new S3ImageDao(s3Client, config.getS3BucketName());
        final PostgresDao postgresDao = new PostgresDao(dbi);

        final ShareItemController shareItemController = new ShareItemController(
                config, imageDao, postgresDao, executorService);
        final UserController userController = new UserController(postgresDao, executorService);


        final ShareItemResource shareItemResource = new ShareItemResource(shareItemController, environment.getObjectMapper());
        final UserResource userResource = new UserResource(userController);
        final ImageResource imageResource = new ImageResource(imageDao);

        final JsonNode categoryHierarchy;
        try (InputStream is = this.getClass().getResourceAsStream("/catalog/car_tool_categories.yml")) {
            categoryHierarchy = new ObjectMapper(new YAMLFactory()).readValue(is, JsonNode.class);
        }

        final CategoryResource categoryResource = new CategoryResource(categoryHierarchy);

        environment.jersey().register(shareItemResource);
        environment.jersey().register(userResource);
        environment.jersey().register(imageResource);
        environment.jersey().register(categoryResource);

        environment.jersey().register(MultiPartConfigProvider.class);
        environment.jersey().register(com.sun.jersey.multipart.impl.MultiPartReaderServerSide.class);

        BearerTokenOAuth2Provider oAuth2Provider = new BearerTokenOAuth2Provider(postgresDao);
        environment.jersey().register(new OAuthProvider<>(oAuth2Provider, config.getAuthConfiguration().getBearerRealm()));


        ClientIdConfiguration clientAuth = config.getClientIdConfiguration();
        if(clientAuth.isEnabled()) {
            final ClientIdRequestFilter clientIdRequestFilter = new ClientIdRequestFilter(clientAuth.getValidClientIds(),
                    clientAuth.getfilterPathPatterns());
            environment.jersey().getResourceConfig().getContainerRequestFilters().add(clientIdRequestFilter);
        }

        environment.healthChecks().register("heartbeat", new HeartbeatHealthCheck());

        // add logging filter
        LoggingFilter loggingFilter = new LoggingFilter();
        environment.jersey().getResourceConfig().getContainerRequestFilters().add(loggingFilter);
        environment.jersey().getResourceConfig().getContainerResponseFilters().add(loggingFilter);


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
                new LinkedBlockingQueue<>(maxPoolSize),
                new ThreadFactoryBuilder().setDaemon(true).setNameFormat(name + "-%s").build(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    private void configureCors(Environment environment) {

        final FilterRegistration.Dynamic filter =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        filter.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,PUT,POST,DELETE,OPTIONS");
        filter.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        filter.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        filter.setInitParameter("allowedHeaders", "Content-Type,Authorization,X-Requested-With,Content-Length,Accept,Origin");
        filter.setInitParameter("allowCredentials", "true");
    }
}
