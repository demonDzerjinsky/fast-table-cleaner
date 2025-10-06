package ru.ssp.api;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.ssp.infra.CustomProperties.getPty;

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
            .withUsername(login)
            .withPassword(pass);

    @Test
    void checkConfigUserAndPassIsNotNull() {
        assertEquals("testuser", login);
        assertEquals("testpass", pass);
    }

    @Test
    void checkContainer() {
        var url = postgres.getJdbcUrl();
        var actualUser = postgres.getUsername();
        var actualPass = postgres.getPassword();
        log.info("container url = {}", url);
        assertAll(
                () -> assertEquals(login, actualUser),
                () -> assertEquals(pass, actualPass),
                () -> assertNotNull(url));
    }

    @Test
    void main() {
        assertTrue(true);
    }
}
