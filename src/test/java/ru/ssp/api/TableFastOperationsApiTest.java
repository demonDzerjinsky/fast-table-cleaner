package ru.ssp.api;

import static ru.ssp.infra.CustomProperties.getPty;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;

import org.assertj.core.api.Assertions;
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
        Assertions.assertThat(postgres.getJdbcUrl()).isEqualTo(getPty("db.url"));
    }
}
