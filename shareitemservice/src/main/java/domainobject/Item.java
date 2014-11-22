package domainobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
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
    private final Range<DateTime> rentalPeriod;
    private final ImmutableList<UUID> imageIds;

    @JsonCreator
    public Item(@JsonProperty("id") UUID id,
                @JsonProperty("itemName") String itemName,
                @JsonProperty("itemDescription") String itemDescription,
                @JsonProperty("rentalPeriod") Range<DateTime> rentalPeriod,
                @JsonProperty("imageIds") List<UUID> imageIds

    ) {
        this.id = id;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.rentalPeriod = rentalPeriod;
        this.imageIds = ImmutableList.copyOf(imageIds);
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
    public Range<DateTime> getRentalPeriod() {
        return rentalPeriod;
    }

    @JsonProperty
    public ImmutableList<UUID> getImageIds() {
        return imageIds;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("itemName", itemName)
                .add("itemDescription", itemDescription)
                .add("rentalPeriod", rentalPeriod)
                .add("imageIds", imageIds)
                .toString();
    }

    public static Item of(RequestItemMetadata requestItemMetadata, List<UUID> imageUuids) {
        return new Item(
                UUID.randomUUID(), requestItemMetadata.getItemName(),
                requestItemMetadata.getItemDescription(),
                Range.closed(requestItemMetadata.getRentalPeriodStart(), requestItemMetadata.getRentalPeriodEnd()),
                imageUuids);
    }
}
