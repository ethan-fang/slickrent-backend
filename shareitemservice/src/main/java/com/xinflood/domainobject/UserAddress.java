package com.xinflood.domainobject;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;

public class UserAddress {
    private final String addressLine1;
    private final Optional<String> addressLine2;
    private final String city;
    private final String state;
    private final String zipCode;
    private final String countryCode;

    @JsonCreator
    public UserAddress(
            @JsonProperty("addressLine1") String addressLine1,
            @JsonProperty("addressLine2") Optional<String> addressLine2,
            @JsonProperty("city") String city,
            @JsonProperty("state") String state,
            @JsonProperty("zipCode") String zipCode,
            @JsonProperty("countryCode") String countryCode) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.countryCode = countryCode;
    }


    @JsonProperty
    public String getAddressLine1() {
        return addressLine1;
    }

    @JsonProperty
    public Optional<String> getAddressLine2() {
        return addressLine2;
    }

    @JsonProperty
    public String getCity() {
        return city;
    }

    @JsonProperty
    public String getState() {
        return state;
    }

    @JsonProperty
    public String getZipCode() {
        return zipCode;
    }

    @JsonProperty
    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("addressLine1", addressLine1)
                .add("addressLine2", addressLine2)
                .add("city", city)
                .add("state", state)
                .add("zipCode", zipCode)
                .add("countryCode", countryCode)
                .toString();
    }
}