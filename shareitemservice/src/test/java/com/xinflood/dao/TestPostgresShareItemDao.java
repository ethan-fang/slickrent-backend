package com.xinflood.dao;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import com.xinflood.DaoHelper;
import com.xinflood.domainobject.Item;
import com.xinflood.domainobject.RentalPricePerHour;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

/**
 */
public class TestPostgresShareItemDao {

    private PostgresDao postgresDao = DaoHelper.getPostgresShareItemDao();

    @Test
    public void testInsertShareItem() {
        Item item = new Item(
                UUID.randomUUID(), "name", "description",
                Optional.of(RentalPricePerHour.random()),
                Optional.of(Range.closed(DateTime.now(), DateTime.now().plusDays(1))),
                ImmutableList.of(UUID.randomUUID()), UUID.randomUUID()
        );

        postgresDao.addShareItem(item, UUID.fromString("e7568b2c-2c0f-480e-9e34-08f9a4b807dd"));
    }


    @Test
    public void testGetShareItem() {
        int numItem = 1;
        List<Item> i = postgresDao.getItems(numItem, 0, Optional.of(UUID.fromString("036ebde5-369d-4cca-ae64-b7e995b2a6d8")));
        assertTrue(i.size()<=numItem);
    }
}
