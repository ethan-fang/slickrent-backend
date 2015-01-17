package com.xinflood.db;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.Argument;
import org.skife.jdbi.v2.tweak.ArgumentFactory;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * Created by xinxinwang on 11/22/14.
 */
public class PostgresUuidSqlArrayArgumentFactory implements ArgumentFactory<SqlArray<UUID>> {

    @Override
    public boolean accepts(Class<?> expectedType, Object value, StatementContext ctx) {
        return value instanceof SqlArray && ((SqlArray)value).getType().isAssignableFrom(UUID.class);
    }

    @Override
    public Argument build(Class<?> expectedType, final SqlArray<UUID> value, StatementContext ctx) {
        return new Argument()
        {
            public void apply(int position,
                              PreparedStatement statement,
                              StatementContext ctx) throws SQLException
            {
                // in postgres no need to (and in fact cannot) free arrays
                Array ary = ctx.getConnection().createArrayOf("uuid", value.getElements());
                statement.setArray(position, ary);
            }
        };
    }
}
