package ru.ssp.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.ssp.infra.CustomProperties.getPty;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Testcontainers
public class ApiIntegrationTest {

    private final static String login = getPty("db.username");
    private final static String pass = getPty("db.password");

    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("pass");

    @Test
    void checkConfigUserAndPassIsNotNull() {
        assertEquals("testuser", login);
        assertEquals("testpass", pass);
    }

    @Test
    void checkContainer() {
        var url = postgres.getJdbcUrl();
        var user = postgres.getUsername();
        var pass = postgres.getPassword();
        log.info("container url = {}", url);
        Assertions.assertAll(
                () -> assertEquals("user", user),
                () -> assertEquals("pass", pass),
                () -> assertNotNull(url));
    }

    @Test
    void main() {
        assertTrue(true);
    }
}
