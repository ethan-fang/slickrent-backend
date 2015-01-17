package com.xinflood.migration;

import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by xinxinwang on 1/17/15.
 */
public class DbMigrationBundle<T extends WithDbMigrationConfiguration> implements ConfiguredBundle<T> {
    @Override
    public void run(T configuration, Environment environment) throws Exception {
        checkNotNull(configuration, "configuration is null");

        final DbMigrationConfiguration dbMigrationConfiguration = checkNotNull(configuration.getDbMigrationConfiguration(), "dbMigrationConfiguration is null");

        if (dbMigrationConfiguration.getIsEnabled()) {
            final DbMigrationRunner migrationRunner = new DbMigrationRunner(dbMigrationConfiguration);
            migrationRunner.migrate();
        }
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {

    }
}
