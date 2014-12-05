package com.xinflood.dao;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.xinflood.DaoHelper;
import com.xinflood.domainobject.Item;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by xinxinwang on 11/22/14.
 */
public class TestPostgresShareItemDao {

    private PostgresDao postgresDao = DaoHelper.getPostgresShareItemDao();

    @Test
    public void testInsertShareItem() {
        Item item = new Item(
                UUID.randomUUID(), "name", "description",
                Range.closed(DateTime.now(), DateTime.now().plusDays(1)),
                ImmutableList.of(UUID.randomUUID())
        );

        postgresDao.addShareItem(item);
    }
}
