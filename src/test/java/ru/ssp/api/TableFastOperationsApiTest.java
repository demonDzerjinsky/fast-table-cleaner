package ru.ssp.api;

import static java.sql.Timestamp.valueOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static ru.ssp.infra.CustomConnectionPool.closePool;
import static ru.ssp.infra.CustomConnectionPool.getConnection;
import static ru.ssp.infra.CustomProperties.getPty;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Ports;

import org.assertj.core.matcher.AssertionMatcher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
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
    void shouldRetainOnlyNewerRecords() throws InterruptedException {
        prepare();
    }

    LocalDateTime prepare() throws InterruptedException {
        // создаем таблицу и заполняем какими то записями с отбивкой времени
        createTable();
        Thread.sleep(1000);
        // вставляем запись с фиксированной отбивкой по которой в последствии будем
        // проверять
        // сколько осталось
        for (int i = 0; i < 10; i++) {
            insertIntoTable(LocalDateTime.now());
            Thread.sleep(100);
        }
        final LocalDateTime dateFrom = LocalDateTime.now();
        insertIntoTable(dateFrom);
        Thread.sleep(100);
        // добавляем еще пару более новых записей (должны остаться после очистки при
        // передаче dateFrom)
        insertIntoTable(LocalDateTime.now());
        insertIntoTable(LocalDateTime.now());
        final int actualCount = getRecordsCount();
        assertTrue(actualCount > 3);
        return dateFrom;
    }

    private int getRecordsCount() {
        String qry = "select count(*) from test_table";
        try (var conn = getConnection(); var stmt = conn.createStatement()) {
            var rs = stmt.executeQuery(qry);
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertIntoTable(LocalDateTime now) {
        final String qry = "insert into test_table values(?, ?, ?)";
        try (var conn = getConnection(); var stmt = conn.prepareStatement(qry)) {
            stmt.setString(1, UUID.randomUUID().toString());
            stmt.setString(2, UUID.randomUUID().toString());
            stmt.setTimestamp(3, valueOf(now));
            var result = stmt.executeUpdate();
            assertThat(result).isEqualTo(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void createTable() {
        try (var conn = getConnection(); var stmt = conn.createStatement()) {
            var result = stmt.execute(
                    "create table test_table (id varchar2(20) primary key, some_data text, event_time timestamp)");
            assertThat(result).isFalse();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void afterAll() {
        closePool();
    }
}
