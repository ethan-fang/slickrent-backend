package com.xinflood.dao;

import com.xinflood.db.DateTimeArgumentFactory;
import com.xinflood.db.ItemResultMapper;
import com.xinflood.db.PostgresUuidSqlArrayArgumentFactory;
import com.xinflood.db.SqlArray;
import com.xinflood.domainobject.Item;
import org.joda.time.DateTimeZone;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.Query;
import org.skife.jdbi.v2.Update;
import org.skife.jdbi.v2.sqlobject.stringtemplate.StringTemplate3StatementLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by xinxinwang on 11/22/14.
 */
public class PostgresShareItemDao implements ShareItemDao {

    private static final Logger LOG = LoggerFactory.getLogger(PostgresShareItemDao.class);
    private final DBI dbi;

    public PostgresShareItemDao(DBI dbi) {
        this.dbi = dbi;

        this.dbi.setStatementLocator(StringTemplate3StatementLocator.builder(this.getClass()).build());
        this.dbi.registerArgumentFactory(DateTimeArgumentFactory.getDateTimeArgumentFactory(DateTimeZone.UTC));
        this.dbi.registerArgumentFactory(new PostgresUuidSqlArrayArgumentFactory());
    }


    @Override
    public void addShareItem(Item item) {
        Handle handle = dbi.open();
        Update update = handle.createStatement("add_item");
        update.bind("id", item.getId());
        update.bind("item_name", item.getItemName());
        update.bind("item_description", item.getItemDescription());
        update.bind("rental_start", item.getRentalPeriod().lowerEndpoint());
        update.bind("rental_end", item.getRentalPeriod().upperEndpoint());
        update.bind("image_uuids", SqlArray.arrayOf(UUID.class, item.getImageUuids()));


        int rowsModified = update.execute();

        if(rowsModified != 1) {
            LOG.error("fail to upload item to db %s", item);
        }

        handle.close();
        return;

    }

    @Override
    public List<Item> getItems(int numItems) {
        Handle handle = dbi.open();
        Query<Map<String, Object>> query = handle.createQuery("retrieve_items");

        if(numItems > 0) {
            query.bind("num_items", numItems);
        }

        return query.map(new ItemResultMapper()).list();
    }

}
