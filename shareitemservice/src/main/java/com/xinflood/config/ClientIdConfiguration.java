package com.xinflood.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableSet;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Configuration for construct client verifications
 */
public class ClientIdConfiguration
{
    private boolean enabled = false;
    private ImmutableSet<UUID> validClientIds = ImmutableSet.of();
    private ImmutableSet<Pattern> filterPathPatterns = ImmutableSet.of();


    public boolean isEnabled()
    {
        return enabled;
    }

    @JsonProperty
    public ClientIdConfiguration setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        return this;
    }

    @NotNull
    public Set<UUID> getValidClientIds()
    {
        return validClientIds;
    }

    @JsonProperty
    public ClientIdConfiguration setValidClientIds(Set<UUID> validClientIds)
    {
        this.validClientIds = ImmutableSet.copyOf(validClientIds);
        return this;
    }

    @NotNull
    public ImmutableSet<Pattern> getfilterPathPatterns()
    {
        return filterPathPatterns;
    }

    @JsonProperty
    public ClientIdConfiguration setFilterPathPatterns(ImmutableSet<Pattern> filterPathPatterns)
    {
        this.filterPathPatterns = filterPathPatterns;
        return this;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("enabled", enabled)
                .add("validClientIds", validClientIds)
                .add("filterPathPatterns", filterPathPatterns)
                .toString();
    }
}