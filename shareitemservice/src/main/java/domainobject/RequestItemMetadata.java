package domainobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import org.joda.time.DateTime;

/**
 * Created by xinxinwang on 11/16/14.
 */
public class RequestItemMetadata {
    private final String itemName;
    private final String itemDescription;
    private final DateTime rentalPeriodStart;
    private final DateTime rentalPeriodEnd;

    @JsonCreator
    public RequestItemMetadata(@JsonProperty("itemName") String itemName,
                               @JsonProperty("itemDescription") String itemDescription,
                               @JsonProperty("rentalPeriodStart") DateTime rentalPeriodStart,
                               @JsonProperty("rentalPeriodEnd") DateTime rentalPeriodEnd) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.rentalPeriodStart = rentalPeriodStart;
        this.rentalPeriodEnd = rentalPeriodEnd;
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
    public DateTime getRentalPeriodStart() {
        return rentalPeriodStart;
    }

    @JsonProperty
    public DateTime getRentalPeriodEnd() {
        return rentalPeriodEnd;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(RequestItemMetadata.class)
                .add("itemName", itemName)
                .add("itemDescription", itemDescription)
                .add("rentalPeriodStart", rentalPeriodStart)
                .add("rentalPeriodEnd", rentalPeriodEnd)
                .toString();
    }
}
