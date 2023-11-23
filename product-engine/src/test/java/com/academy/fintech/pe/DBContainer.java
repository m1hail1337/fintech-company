package com.academy.fintech.pe;

import org.testcontainers.containers.PostgreSQLContainer;

public class DBContainer extends PostgreSQLContainer<DBContainer> {
    private static final String IMAGE_VERSION = "postgres:14.1-alpine";

    private static DBContainer container;

    private DBContainer() {
        super(IMAGE_VERSION);
    }

    public static DBContainer getInstance() {
        if (container == null) {
            container = new DBContainer()
                    .withDatabaseName("test-db")
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
