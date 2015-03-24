package com.xinflood.domainobject;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile {

    private final String username;
    private final String email;
    private final UUID photoUuid;
    private final String fullName;
    private final String phoneNumber;
    private final UserAddress userAddress;

    @JsonCreator
    public UserProfile(
            @JsonProperty("username") String username,
            @JsonProperty("email") String email,
            @JsonProperty("photoUuid") UUID photoUuid,
            @JsonProperty("fullName") String fullName,
            @JsonProperty("phoneNumber") String phoneNumber,
            @JsonProperty("userAddress") UserAddress userAddress) {
        this.username = checkNotNull(username, "username is null");
        this.email = checkNotNull(email, "email is null");
        this.photoUuid = checkNotNull(photoUuid, "photoUuid is null");
        this.fullName = checkNotNull(fullName, "fullName is null");
        this.phoneNumber = checkNotNull(phoneNumber, "phoneNumber is null");
        this.userAddress = checkNotNull(userAddress, "userAddress is null");
    }

    @JsonProperty
    public String getUsername() {
        return username;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }

    @JsonProperty
    public UUID getPhotoUuid() {
        return photoUuid;
    }

    @JsonProperty
    public String getFullName() {
        return fullName;
    }

    @JsonProperty
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonProperty
    public UserAddress getUserAddress() {
        return userAddress;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("username", username)
                .add("email", email)
                .add("photoUuid", photoUuid)
                .add("fullName", fullName)
                .add("phoneNumber", phoneNumber)
                .add("userAddress", userAddress)
                .toString();
    }



}
