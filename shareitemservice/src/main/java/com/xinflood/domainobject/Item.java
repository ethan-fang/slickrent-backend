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

/**
 */
public class Item {

    private final UUID id;
    private final String itemName;
    private final String itemDescription;
    private final Optional<RentalPricePerHour> price;
    private final Optional<Range<DateTime>> rentalPeriod;
    private final ImmutableList<UUID> imageUuids;

    @JsonCreator
    public Item(@JsonProperty("id") UUID id,
                @JsonProperty("itemName") String itemName,
                @JsonProperty("itemDescription") String itemDescription,
                @JsonProperty("price") Optional<RentalPricePerHour> price,
                @JsonProperty("rentalPeriod") Optional<Range<DateTime>> rentalPeriod,
                @JsonProperty("imageUuids") List<UUID> imageUuids

    ) {
        this.id = id;
        this.itemName = itemName;
        this.price = price;
        this.itemDescription = itemDescription;
        this.rentalPeriod = rentalPeriod;
        this.imageUuids = ImmutableList.copyOf(imageUuids);
    }

    @JsonProperty
    public UUID getId() {
        return id;
    }

    @JsonProperty
    public String getItemName() {
        return itemName;
    }

    @JsonProperty
    public String getItemDescription() {
        return itemDescription;
    }

    @JsonProperty
    public Optional<Range<DateTime>> getRentalPeriod() {
        return rentalPeriod;
    }

    @JsonProperty
    public ImmutableList<UUID> getImageUuids() {
        return imageUuids;
    }

    @JsonProperty
    public Optional<RentalPricePerHour> getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("itemName", itemName)
                .add("itemDescription", itemDescription)
                .add("rentalPeriod", rentalPeriod)
                .add("imageUuids", imageUuids)
                .toString();
    }

    public static Item of(RequestItemMetadata requestItemMetadata) {

        Optional<RentalPricePerHour> rentalPricePerHour = Optional.absent();
        if(requestItemMetadata.getPricePerHourInCent().isPresent()) {
            rentalPricePerHour = Optional.of(RentalPricePerHour.ofDollarPerHour(requestItemMetadata.getPricePerHourInCent().get()));
        }

        Optional<Range<DateTime>> rentalRange = Optional.absent();
        if(requestItemMetadata.getRentalRanges().isPresent()) {
            rentalRange = Optional.of(requestItemMetadata.getRentalRanges().get().get(0));
        }

        return new Item(
                UUID.randomUUID(),
                requestItemMetadata.getItemName(),
                requestItemMetadata.getItemDescription(),
                rentalPricePerHour,
                rentalRange,
                requestItemMetadata.getImageUuids());
    }

}
