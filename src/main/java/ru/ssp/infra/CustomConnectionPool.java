package ru.ssp.infra;

import static java.lang.Integer.valueOf;
import static ru.ssp.infra.CustomProperties.getPty;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariConfig;

/**
 * пул соединений с БД.
 */
public final class CustomConnectionPool {

    /**
     * ключ - название свойства.
     */
    private static final String JDBC_URL_PTY = "db.url";

    /**
     * ключ - название свойства.
     */
    private static final String USER_NAME_PTY = "db.username";

    /**
     * ключ - название свойства.
     */
    private static final String USER_PASS_PTY = "db.password";

    /**
     * ключ - название свойства.
     */
    private static final String POOL_SIZE_PTY = "db.poolsize";

    /**
     * пул соединений.
     */
    private static HikariDataSource dataSource;

    static {
        configure();
    }

    private CustomConnectionPool() {
    }

    private static void configure() {
        final var config = new HikariConfig();
        config.setJdbcUrl(getPty(JDBC_URL_PTY));
        config.setUsername(getPty(USER_NAME_PTY));
        config.setPassword(getPty(USER_PASS_PTY));
        config.setMaximumPoolSize(valueOf(getPty(POOL_SIZE_PTY)));
        dataSource = new HikariDataSource(config);
    }

    /**
     * возвращает новое соединение из БД.
     * @return соединение
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * закрывает пул соединений.
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
