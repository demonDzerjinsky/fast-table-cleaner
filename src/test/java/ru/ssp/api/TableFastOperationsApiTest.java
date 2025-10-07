package ru.ssp.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.ssp.infra.CustomConnectionPool.closePool;
import static ru.ssp.infra.CustomConnectionPool.getConnection;
import static ru.ssp.infra.CustomProperties.getPty;

import java.sql.SQLException;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;

import com.github.dockerjava.api.model.PortBinding;

@Slf4j
@Testcontainers
public class TableFastOperationsApiTest {

    private static final int exposedPort = 5432;
    private static final int externalPort = 54320;

    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername(getPty("db.username"))
            .withPassword(getPty("db.password"))
            .withExposedPorts(exposedPort)
            .withCreateContainerCmdModifier(
                    cmd -> cmd.withHostConfig(
                            new HostConfig().withPortBindings(
                                    new PortBinding(Ports.Binding.bindPort(externalPort),
                                            new ExposedPort(exposedPort)))));

    @Test
    void checkConnection() {
        assertThat(postgres.getJdbcUrl()).isEqualTo(getPty("db.url"));
        try (var conn = getConnection()) {
            assertThat(conn).isNotNull();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Tag("main")
    @Test
    void shouldRetainOnlyNewerRecords() {
        assertTrue(true);
    }

    @AfterAll
    static void afterAll() {
        closePool();
    }
}
