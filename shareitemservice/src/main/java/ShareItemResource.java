import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import domainobject.Item;
import domainobject.RequestItemMetadata;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by xinxinwang on 11/16/14.
 */
@Path("/shareitem")
public class ShareItemResource {

    private final ShareItemController shareItemController;
    private final ObjectMapper objectMapper;

    public ShareItemResource(ShareItemController shareItemController, ObjectMapper objectMapper) {
        this.shareItemController = shareItemController;
        this.objectMapper = objectMapper;
    }

    @PUT
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addItem(
            FormDataMultiPart formDataMultiPart
    ) throws IOException {

        checkNotNull(formDataMultiPart.getFields("file"));
        checkNotNull(formDataMultiPart.getFields("metadata"));



        Collection<InputStream> images = Collections2.transform(formDataMultiPart.getFields("file"), new Function<FormDataBodyPart, InputStream>() {
            @Override
            public InputStream apply(FormDataBodyPart input) {
                return input.getValueAs(InputStream.class);
            }
        });

        String metadata = formDataMultiPart.getField("metadata").getValueAs(String.class);
        RequestItemMetadata requestItemMetadata = objectMapper.readValue(metadata, RequestItemMetadata.class);

        Item created = shareItemController.addNewItem(requestItemMetadata, images);
        return Response.ok(ImmutableMap.of("item", created)).build();
    }

}
