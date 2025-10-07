package ru.ssp.api;

import static java.sql.Timestamp.valueOf;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.ssp.infra.CustomConnectionPool.closePool;
import static ru.ssp.infra.CustomConnectionPool.getConnection;
import static ru.ssp.infra.CustomProperties.getPty;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import lombok.extern.slf4j.Slf4j;
import ru.ssp.dto.TruncOlderThanRequest;
import ru.ssp.dto.TruncOlderThanResponse;

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

    /**
     * успешный кейс
     * 11 записей старше чем переданная дата-время
     * посде очистки осталось 2
     */ 
    @Tag("main")
    @Test
    void shouldRetainOnlyNewerRecords() {
        var dateFrom = prepare(); // 13 records created and 2 after dateFrom
        var response = TableFastOperationsApi.truncateOlderThan(
                new TruncOlderThanRequest("test_table", "event_time", dateFrom));
        assertThat(response.status()).isEqualTo(TruncOlderThanResponse.Status.OK);
        var retained = getRecordsCount();
        assertThat(retained).isEqualTo(2);
    }

    /**
     * возвр контракт с ошибкой если таблица не существует в БД
     * данные остаются в сохранности
     */
    @Test
    void shouldFailWhenBadTableName() {
        var dateFrom = prepare(); // 13 records created and 2 after dateFrom
        var response = TableFastOperationsApi.truncateOlderThan(
                new TruncOlderThanRequest("not_exist_table", "event_time", dateFrom));
        assertThat(response.status()).isEqualTo(TruncOlderThanResponse.Status.FAIL);
        var retained = getRecordsCount();
        assertThat(retained).isEqualTo(13);
    }

    /**
     * возвр контракт с ошибкой если колонка не формата timestamp
     * данные остаются в сохранности
     */
    @Test
    void shouldFailWhenBadColFormat() {
        var dateFrom = prepare(); // 13 records created and 2 after dateFrom
        var response = TableFastOperationsApi.truncateOlderThan(
                new TruncOlderThanRequest("test_table", "some_data", dateFrom));//not timestamp col
        assertThat(response.status()).isEqualTo(TruncOlderThanResponse.Status.FAIL);
        var retained = getRecordsCount();
        assertThat(retained).isEqualTo(13);
    }


    LocalDateTime prepare() {
        try {
            // создаем таблицу и заполняем какими то записями с отбивкой времени
            createTable();
            Thread.sleep(1000);
            for (int i = 0; i < 10; i++) {
                insertIntoTable(now());
                Thread.sleep(100);
            }
            // вставляем запись с фиксированной отбивкой по которой в последствии будем
            // проверять сколько осталось
            final LocalDateTime dateFrom = now();
            insertIntoTable(dateFrom);
            Thread.sleep(100);
            // добавляем еще пару более новых записей (должны остаться после очистки при
            // передаче dateFrom)
            insertIntoTable(now());
            insertIntoTable(now());
            final int actualCount = getRecordsCount();
            assertTrue(actualCount > 3);
            return dateFrom;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
                    "create table test_table (id varchar(60) primary key, some_data text, event_time timestamp)");
            assertThat(result).isFalse();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void afterEach() {
        try (var conn = getConnection(); var stmt = conn.createStatement()) {
            var result = stmt.execute("drop table if exists test_table");
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
