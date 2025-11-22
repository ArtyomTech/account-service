package com.tuum.account.service;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class PostgreSQLTestBase {

  protected static final PostgreSQLContainer<?> postgres;

  static {
    @SuppressWarnings("resource")
    PostgreSQLContainer<?> container =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("account-service-test-db")
            .withUsername("postgres")
            .withPassword("postgres");
    container.start();
    postgres = container;
  }

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.flyway.enabled", () -> "true");
  }
}
