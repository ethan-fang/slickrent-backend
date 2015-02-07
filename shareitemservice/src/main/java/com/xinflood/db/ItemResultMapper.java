package com.xinflood.db;

import com.google.common.collect.Range;
import com.xinflood.domainobject.Item;
import com.xinflood.domainobject.RentalPricePerHour;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by xinxinwang on 11/23/14.
 */
public class ItemResultMapper implements ResultSetMapper<Item> {

    public static final ItemResultMapper INSTANCE = new ItemResultMapper();

    @Override
    public Item map(int index, ResultSet r, StatementContext ctx) throws SQLException {

        UUID id = UUID.fromString(r.getString("id"));
        String itemName = r.getString("item_name");
        String itemDescription = r.getString("item_description");
        double priceInUSCentPerMinute = r.getDouble("price_usd_cent_per_min");
        DateTime rentalStart = new DateTime(r.getTimestamp("rental_start"));
        DateTime rentalEnd = new DateTime(r.getTimestamp("rental_end"));
        List<UUID> imageUuids = Arrays.asList((UUID[]) r.getArray("image_uuids").getArray());

        return new Item(id, itemName, RentalPricePerHour.ofDollarPerHour(priceInUSCentPerMinute), itemDescription, Range.closed(rentalStart, rentalEnd), imageUuids);
    }

}
