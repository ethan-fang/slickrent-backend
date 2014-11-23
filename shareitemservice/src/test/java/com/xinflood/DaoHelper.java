package com.xinflood;

import com.google.common.base.Splitter;
import com.google.common.io.CharStreams;
import com.xinflood.dao.PostgresShareItemDao;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import org.skife.jdbi.v2.tweak.HandleCallback;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

/**
 * Created by xinxinwang on 11/23/14.
 */
public final class DaoHelper {
    private DaoHelper() {
        new AssertionError("Don't instantiate this");
    }

    public static PostgresShareItemDao getPostgresShareItemDao() {
        DBI dbi = new DBI("jdbc:postgresql://itemsharedbinstance.cujcjpmqj3ya.us-east-1.rds.amazonaws.com:5432/itemsharedb?prepareThreshold=0", "xin041619", "xin083333");
        return new PostgresShareItemDao(dbi);
    }

    private static void loadSqlResource(final DBI dbi, final String sqlFile)
    {
        dbi.withHandle(new HandleCallback<Void>()
        {
            @Override
            public Void withHandle(final Handle handle)
                    throws Exception
            {
                try (Reader sqlReader = new InputStreamReader(this.getClass().getResourceAsStream(sqlFile), StandardCharsets.UTF_8)) {
                    final String sql = CharStreams.toString(sqlReader);
                    for (final String statement : Splitter.on(";").omitEmptyStrings().split(sql)) {
                        handle.createStatement(statement).execute();
                    }

                    return null;
                }
            }
        });
    }

}
