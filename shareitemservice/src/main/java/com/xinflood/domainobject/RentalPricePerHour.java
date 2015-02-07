package com.xinflood.domainobject;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.annotations.VisibleForTesting;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.time.Duration;

import java.math.RoundingMode;

/**
 * A price class that deals with money
 */
public class RentalPricePerHour {
    private final Money amount;

    public static RentalPricePerHour of(double priceInDollar, Duration perTimeUnit) {
        return new RentalPricePerHour(Money.of(CurrencyUnit.USD, priceInDollar).dividedBy(perTimeUnit.getStandardHours(), RoundingMode.CEILING));
    }

    public static RentalPricePerHour ofDollarPerHour(double priceInDollarPerHour) {
        return new RentalPricePerHour(Money.of(CurrencyUnit.USD, priceInDollarPerHour));
    }

    public static RentalPricePerHour ofCentPerMinute(double priceInCentPerMinute) {

        return new RentalPricePerHour(Money.of(CurrencyUnit.USD, priceInCentPerMinute).multipliedBy(60*100));
    }

    private RentalPricePerHour(Money amount) {
        this.amount = amount;
    }


    @JsonProperty
    public Money getAmount() {
        return amount;
    }


    @VisibleForTesting
    public static RentalPricePerHour random() {
        return RentalPricePerHour.of(100, Duration.standardHours(1));
    }
}
