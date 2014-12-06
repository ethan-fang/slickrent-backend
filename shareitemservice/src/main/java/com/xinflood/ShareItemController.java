package com.xinflood;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;
import com.xinflood.config.ShareItemServerConfiguration;
import com.xinflood.dao.ImageDao;
import com.xinflood.dao.ShareItemDao;
import com.xinflood.domainobject.Item;
import com.xinflood.domainobject.RequestItemMetadata;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Created by xinxinwang on 11/16/14.
 */
public class ShareItemController {
    private final ImageDao imageDao;
    private final ShareItemDao shareItemDao;
    private final ShareItemServerConfiguration configuration;
    private final ExecutorService executorService;


    public ShareItemController(ShareItemServerConfiguration configuration, ImageDao imageDao,
                               ShareItemDao shareItemDao, ExecutorService executorService) {
        this.configuration = configuration;
        this.imageDao = imageDao;
        this.shareItemDao = shareItemDao;
        this.executorService = executorService;
    }

    public Item addNewItem(UUID userId, RequestItemMetadata requestItemMetadata, Collection<InputStream> imageUploads) throws IOException {
        Item item = Item.of(requestItemMetadata, uploadImages(imageUploads));
        executorService.submit(new AddItemTask(item, shareItemDao, userId));
        return item;
    }


    public List<Item> getItems(int numItems, Optional<UUID> userId) throws ExecutionException, InterruptedException {
        Future<List<Item>> future = executorService.submit(new GetItemsTask(shareItemDao, numItems, userId));
        return future.get();
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

    private static class GetItemsTask implements Callable<List<Item>> {

        private final ShareItemDao shareItemDao;
        private final int numItems;
        private final Optional<UUID> userId;

        private GetItemsTask(ShareItemDao shareItemDao, int numItems, Optional<UUID> userId) {
            this.shareItemDao = shareItemDao;
            this.numItems = numItems;
            this.userId = userId;
        }

        @Override
        public List<Item> call() throws Exception {
            return shareItemDao.getItems(numItems, userId);
        }
    }

    private static class AddItemTask implements Callable<Boolean> {

        private final Item item;
        private final ShareItemDao shareItemDao;
        private final UUID userId;

        private AddItemTask(Item item, ShareItemDao shareItemDao, UUID userId) {
            this.item = item;
            this.shareItemDao = shareItemDao;
            this.userId = userId;
        }


        @Override
        public Boolean call() throws Exception {
            shareItemDao.addShareItem(item, userId);
            return true;
        }
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
