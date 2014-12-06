package com.xinflood.dao;

import com.google.common.base.Optional;
import com.xinflood.domainobject.Item;

import java.util.List;
import java.util.UUID;

/**
 * Created by xinxinwang on 11/22/14.
 */
public interface ShareItemDao {
    void addShareItem(Item item, UUID userId);

    List<Item> getItems(int numItems, Optional<UUID> userId);
}
