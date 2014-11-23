package com.xinflood.dao;

import com.xinflood.domainobject.Item;

import java.util.List;

/**
 * Created by xinxinwang on 11/22/14.
 */
public interface ShareItemDao {
    void addShareItem(Item item);

    List<Item> getItems(int numItems);
}
