import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import dao.ImageDao;
import domainobject.Item;
import domainobject.RequestItemMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;

/**
 * Created by xinxinwang on 11/16/14.
 */
public class ShareItemController {
    private final ImageDao imageDao;
    private final ShareItemServerConfiguration configuration;
    private final ExecutorService executorService;


    public ShareItemController(ShareItemServerConfiguration configuration, ImageDao imageDao, ExecutorService executorService) {
        this.configuration = configuration;
        this.imageDao = imageDao;
        this.executorService = executorService;
    }

    public Item addNewItem(RequestItemMetadata requestItemMetadata, Collection<InputStream> imageUploads) throws IOException {
        return Item.of(requestItemMetadata, uploadImages(imageUploads));
    }


    private List<UUID> uploadImages(Collection<InputStream> imageUploads) throws IOException {

        ImmutableList.Builder<UUID> builder = ImmutableList.builder();
        for(InputStream image: imageUploads) {
            UUID imageUUID = UUID.randomUUID();
            builder.add(imageUUID);
            executorService.submit(new UploadImageTask(imageDao, ByteStreams.toByteArray(image), imageUUID));
        }
        return builder.build();
    }

    private static class UploadImageTask implements Callable<Boolean> {

        private final ImageDao imageDao;
        private final byte[] image;
        private final UUID key;

        public UploadImageTask(ImageDao imageDao, byte[] image, UUID key) {
            this.imageDao = imageDao;
            this.image = image;
            this.key = key;
        }

        @Override
        public Boolean call() {
            try {
                imageDao.putImage(key.toString(), image);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

}
