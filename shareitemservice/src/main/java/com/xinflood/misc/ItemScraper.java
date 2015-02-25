package com.xinflood.misc;

import com.xinflood.domainobject.Item;

import java.io.IOException;
import java.util.List;

/**
 * Created by xinxinwang on 2/7/15.
 */
public interface ItemScraper {
    List<Item> getItems() throws IOException;
}
