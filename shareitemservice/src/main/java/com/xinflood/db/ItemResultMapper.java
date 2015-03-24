package com.xinflood.db;

import com.google.common.base.Optional;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import com.xinflood.domainobject.Item;
import com.xinflood.domainobject.RentalPricePerHour;
import org.joda.time.DateTime;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

        Optional<RentalPricePerHour> rentalPricePerHour = Optional.absent();
        if(r.getDouble("price_usd_cent_per_min")> 0) {
            rentalPricePerHour = Optional.of(RentalPricePerHour.ofDollarPerHour(r.getDouble("price_usd_cent_per_min")));
        };

        Optional<Range<DateTime>> rentalPeriod = Optional.absent();
        Timestamp rentalStart = r.getTimestamp("rental_start");
        Timestamp rentalEnd = r.getTimestamp("rental_end");

        if(rentalStart != null && rentalEnd != null) {
            rentalPeriod = Optional.of(Range.closed(new DateTime(rentalStart), new DateTime(rentalEnd)));
        } else if(rentalStart !=null ) {
            rentalPeriod = Optional.of(Range.downTo(new DateTime(rentalStart), BoundType.CLOSED));
        } else {
            rentalPeriod = Optional.of(Range.upTo(new DateTime(rentalEnd), BoundType.CLOSED));
        }

        List<UUID> imageUuids = Arrays.asList((UUID[]) r.getArray("image_uuids").getArray());
        UUID ownerId = UUID.fromString(r.getString("user_id"));

        return new Item(id, itemName, itemDescription, rentalPricePerHour, rentalPeriod, imageUuids, ownerId);
    }

}
