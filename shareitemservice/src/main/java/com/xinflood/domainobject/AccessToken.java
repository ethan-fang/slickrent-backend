package com.xinflood.domainobject;

import com.google.common.base.MoreObjects;
import org.joda.time.DateTime;

import java.util.UUID;

public class AccessToken {
    private final UUID accessTokenId;
    private final UUID userId;
    private final DateTime lastAccessUTC;

    public AccessToken(UUID accessTokenId, UUID userId, DateTime lastAccessUTC) {
        this.accessTokenId = accessTokenId;
        this.userId = userId;
        this.lastAccessUTC = lastAccessUTC;
    }

    public UUID getAccessTokenId() {
        return accessTokenId;
    }

    public UUID getUserId() {
        return userId;
    }

    public DateTime getLastAccessUTC() {
        return lastAccessUTC;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("accessTokenId", accessTokenId)
                .add("userId", userId)
                .add("lastAccessUTC", lastAccessUTC)
                .toString();
    }
}
