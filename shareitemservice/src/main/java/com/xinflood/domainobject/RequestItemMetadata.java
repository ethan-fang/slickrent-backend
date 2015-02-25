package com.xinflood.domainobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Range;
import org.joda.time.DateTime;

import java.util.List;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by xinxinwang on 11/16/14.
 */
public class RequestItemMetadata {
    private final String itemName;
    private final String itemDescription;
    private final Optional<Double> pricePerHourInCent;
    private final ImmutableList<UUID> imageUuids;
    private final int quantity;
    private final Optional<List<Range<DateTime>>> rentalRanges;

    @JsonCreator
    public RequestItemMetadata(@JsonProperty("itemName") String itemName,
                               @JsonProperty("itemDescription") String itemDescription,
                               @JsonProperty("pricePerHour") Optional<Double> pricePerHourInCent,
                               @JsonProperty("images") List<UUID> imageUuids,
                               @JsonProperty("quantity") int quantity,
                               @JsonProperty("rentalPeriods") Optional<List<Range<DateTime>>> rentalRanges) {
        this.itemName = checkNotNull(itemName);
        this.itemDescription = checkNotNull(itemDescription);

        this.pricePerHourInCent = pricePerHourInCent;

        checkArgument(quantity > 0, "quantity %f should be greater than 0", quantity);
        this.quantity =  quantity;

        this.imageUuids = ImmutableList.copyOf(checkNotNull(imageUuids));
        this.rentalRanges = checkNotNull(rentalRanges);
    }


    @JsonProperty
    public String getItemName() {
        return itemName;
    }

    @JsonProperty
    public String getItemDescription() {
        return itemDescription;
    }

    @JsonProperty("pricePerHour")
    public Optional<Double> getPricePerHourInCent() {
        return pricePerHourInCent;
    }

    @JsonProperty("images")
    public ImmutableList<UUID> getImageUuids() {
        return imageUuids;
    }

    @JsonProperty
    public int getQuantity() {
        return quantity;
    }

    @JsonProperty("rentalPeriods")
    public Optional<List<Range<DateTime>>> getRentalRanges() {
        return rentalRanges;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("itemName", itemName)
                .add("itemDescription", itemDescription)
                .add("pricePerHourInCent", pricePerHourInCent)
                .add("imageUuids", imageUuids)
                .add("quantity", quantity)
                .add("rentalRanges", rentalRanges)
                .toString();
    }
}
