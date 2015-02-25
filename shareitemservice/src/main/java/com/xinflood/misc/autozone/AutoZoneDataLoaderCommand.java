package com.xinflood.misc.autozone;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.common.base.Optional;
import com.google.common.collect.Range;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.xinflood.config.ShareItemServerConfiguration;
import com.xinflood.dao.S3ImageDao;
import com.xinflood.domainobject.RequestItemMetadata;
import com.xinflood.misc.ItemScraper;
import io.dropwizard.Application;
import io.dropwizard.cli.EnvironmentCommand;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.setup.Environment;
import net.sourceforge.argparse4j.inf.Namespace;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Created by xinxinwang on 2/7/15.
 */
public class AutoZoneDataLoaderCommand<T extends ShareItemServerConfiguration> extends EnvironmentCommand<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AutoZoneDataLoaderCommand.class);
    /**
     * Creates a new environment command.
     *
     * @param application the application providing this command
     * @param name        the name of the command, used for command line invocation
     * @param description a description of the command's purpose
     */
    public AutoZoneDataLoaderCommand(Application<T> application, String name, String description) {
        super(application, name, description);
    }

    @Override
    protected void run(Environment environment, Namespace namespace, T config) throws Exception {

        final AmazonS3Client s3Client = new AmazonS3Client(
                new BasicAWSCredentials(config.getAwsAccessKeyId(), config.getAwsSecretAccesskey()));

        final S3ImageDao imageDao = new S3ImageDao(s3Client, config.getS3BucketName());

        final Client jerseyClient = new JerseyClientBuilder(environment).build("http-client");
        jerseyClient.setReadTimeout(2000);

        ItemScraper itemScraper = new AutoZoneScraper(imageDao, jerseyClient);

        //hard code my auth header
        WebResource resource = jerseyClient.resource("http://localhost:8080/api/shareitem/7ffc2295-6875-4f40-bc65-827b8fd4535b?clientId=e7568b2c-2c0f-480e-9e34-08f9a4b807dc");




        itemScraper.getItems().forEach(item -> {
            RequestItemMetadata requestItemMetadata = new RequestItemMetadata(
                    item.getItemName(),
                    item.getItemDescription(),
                    Optional.<Double>absent(),
                    item.getImageUuids(),
                    1,
                    Optional.<List<Range<DateTime>>>absent());

            WebResource.Builder builder = resource.header("Authorization", "bearer c2hhcmUyMDE0LTEyLTE0VDE3OjU3OjMzLjI5MloxNTE4ODgwMjU1")
                    .type(MediaType.APPLICATION_JSON_TYPE)
                    .accept(MediaType.APPLICATION_JSON_TYPE);

            ClientResponse response = builder.entity(requestItemMetadata).post(ClientResponse.class);
            System.out.println(response);
        });
    }
}
