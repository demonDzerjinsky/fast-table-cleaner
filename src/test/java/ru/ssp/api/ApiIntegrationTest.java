package ru.ssp.api;

import static java.lang.Integer.valueOf;
import static java.lang.System.out;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
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

@Slf4j
@Testcontainers
public class ApiIntegrationTest {

    private final static String login = getPty("db.username");
    private final static String pass = getPty("db.password");
    private final static Integer pool = valueOf(getPty("db.poolsize"));
    private final static String dbname = "testdb";
    private static HikariDataSource dataSource;

    @Container
    private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName(dbname)
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
        createTable();
        checkTableMeta();
        assertTrue(true);
    }

    void createTable() {
        try (var connection = dataSource.getConnection();
                var stmt = connection.createStatement()) {
            boolean result = stmt.execute(
                    """
                            CREATE TABLE test_table
                            (id SERIAL PRIMARY KEY, some_data TEXT, event_timestamp TIMESTAMP NOT NULL);
                            """);
            assertFalse(result);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void checkTableMeta() {
        try (var connection = dataSource.getConnection()) {
            var meta = connection.getMetaData();
            var cts = meta.getCatalogs();
            while (cts.next()) {
                String catName = cts.getString("TABLE_CAT");
                out.println(catName);
                if (catName.equals(dbname)) {
                    var sms = meta.getSchemas();
                    while (sms.next()) {
                        var schemName = sms.getString("TABLE_SCHEM");
                        out.print("\t");
                        out.println(schemName);
                        if (schemName.equals("public")) {
                            var tbs = meta.getTables(catName, schemName, null, new String[] { "TABLE" });
                            while (tbs.next()) {
                                var tabName = tbs.getString("TABLE_NAME");
                                out.print("\t\t");
                                out.println(tabName);
                                if (tabName.equals("test_table")) {
                                    var cls = meta.getColumns(catName, schemName, tabName, null);
                                    while (cls.next()) {
                                        var colName = cls.getString("COLUMN_NAME");
                                        var colTypeName = cls.getString("TYPE_NAME");
                                        out.print("\t\t\t");
                                        out.print(colName);
                                        out.print(": ");
                                        out.println(colTypeName);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            var cds = meta.getColumns(connection.getCatalog(), connection.getSchema(), "test_table",
                    "event_timestamp");
            if (cds.next()) {
                var actualColTypeName = cds.getString("TYPE_NAME");
                assertEquals("timestamp", actualColTypeName);
            } else {
                fail();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void afterAll() {
        if (dataSource != null && !dataSource.isClosed()) {
            log.info("closing connection pool");
            dataSource.close();
        }
    }
}
