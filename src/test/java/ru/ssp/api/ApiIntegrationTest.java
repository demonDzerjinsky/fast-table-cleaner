package ru.ssp.api;

import static java.lang.Integer.valueOf;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.ssp.infra.CustomProperties.getPty;

import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;
import ru.ssp.infra.CustomConnectionPool;

@Slf4j
@Testcontainers
public class ApiIntegrationTest {

    private final static String login = getPty("db.username");
    private final static String pass = getPty("db.password");
    private final static Integer pool = valueOf(getPty("db.poolsize"));
    private static HikariDataSource dataSource;

    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername(login)
            .withPassword(pass);

    @BeforeAll
    static void beforeAll() {
        final var config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(login);
        config.setPassword(pass);
        config.setMaximumPoolSize(pool);
        dataSource = new HikariDataSource(config);
    }

    @Test
    void checkConfigUserAndPassIsNotNull() {
        assertEquals("testuser", login);
        assertEquals("testpass", pass);
    }

    @Test
    void checkConnection() throws SQLException {
        assertNotNull(dataSource);
        try (var connection = dataSource.getConnection()) {
            assertNotNull(connection);
        }
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

    @AfterAll
    static void afterAll() {
        if (dataSource != null && !dataSource.isClosed()) {
            log.info("closing connection pool");
            dataSource.close();
        }
    }
}
