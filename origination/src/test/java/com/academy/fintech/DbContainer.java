package com.academy.fintech;

import org.testcontainers.containers.PostgreSQLContainer;

public class DbContainer extends PostgreSQLContainer<DbContainer> {
    private static final String IMAGE_VERSION = "postgres:14.1-alpine";

    private static DbContainer container;

    private DbContainer() {
        super(IMAGE_VERSION);
    }

    public static DbContainer getInstance() {
        if (container == null) {
            container = new DbContainer()
                    .withDatabaseName("origination-test-db")
                    .withUsername("test")
                    .withPassword("test")
                    .withReuse(true);
        }
        return container;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void stop() {
        super.stop();
    }
}
