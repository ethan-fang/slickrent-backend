package com.xinflood;

import io.dropwizard.db.DataSourceFactory;

/**
 * Created by xinxinwang on 12/3/14.
 */
public interface WithDatabaseConfiguration {
    DataSourceFactory getDatabase();
}
