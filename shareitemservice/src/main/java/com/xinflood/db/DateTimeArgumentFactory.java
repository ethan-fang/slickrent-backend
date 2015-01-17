package com.xinflood.db;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import javax.annotation.Nullable;

import static com.google.common.base.Preconditions.checkNotNull;

public final class DateTimeArgumentFactory
{
    private DateTimeArgumentFactory()
    {
        throw new AssertionError("do not instantiate");
    }

    public static ArgumentFactory<ReadableInstant> getDateTimeArgumentFactory(final DateTimeZone zone)
    {
        checkNotNull(zone, "zone is null");

        return new ArgumentFactory<ReadableInstant>() {
            @Override
            public boolean accepts(final Class<?> expectedType, final Object value, final StatementContext ctx)
            {
                return value instanceof ReadableInstant;
            }

            @Override
            public Argument build(final Class<?> expectedType, @Nullable final ReadableInstant value, final StatementContext ctx)
            {
                if (value == null || value.getZone().equals(zone)) {
                    return new DateTimeArgument(value);
                }
                else {
                    return new DateTimeArgument(new DateTime(value).withZone(zone));
                }
            }
        };
    }
}
