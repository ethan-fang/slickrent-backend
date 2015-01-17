package com.xinflood.migration;

import com.google.common.base.Strings;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class DbMigrationRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DbMigrationRunner.class);

    private final DbMigrationConfiguration migrationConfiguration;

    public DbMigrationRunner(final DbMigrationConfiguration migrationConfiguration) {
        this.migrationConfiguration = migrationConfiguration;
    }

    public void migrate() {
        try {
            Flyway flyway = new Flyway();

            checkNotNull(migrationConfiguration.getDataSource(), "dataSource is null for migrationConfiguration");
            checkNotNull(migrationConfiguration.getSchema(), "schema is null for migrationConfiguration");

            flyway.setDataSource(migrationConfiguration.getDataSource(), migrationConfiguration.getUser(), migrationConfiguration.getPassword());

            //by default, it migrates to the latest version
            String targetVersion = migrationConfiguration.getTargetVersion();
            if (!Strings.isNullOrEmpty(targetVersion)) {
                flyway.setTarget(targetVersion);
            }

            //by default, it looks for scripts in "resources/resources.db/migration
            String location = migrationConfiguration.getLocation();
            if (!Strings.isNullOrEmpty(location)) {
                flyway.setLocations(location);
            }

            flyway.setSchemas(migrationConfiguration.getSchema());
            flyway.setInitOnMigrate(true);
            flyway.setInitVersion("1");
            flyway.setInitDescription("Base version");
            flyway.setValidateOnMigrate(false);
            flyway.migrate();
            LOGGER.info("Current schema version is {}", flyway.info().current().getVersion().toString());

        } catch (FlywayException e) {
            LOGGER.error("Error occurred while migrate database", e);
            throw e;
        }
    }
}