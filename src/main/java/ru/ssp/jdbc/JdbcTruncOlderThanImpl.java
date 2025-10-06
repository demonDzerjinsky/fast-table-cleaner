package ru.ssp.jdbc;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import ru.ssp.exceptions.CustomSQLException;
import ru.ssp.impl.TruncOlderThanExecr;
import ru.ssp.infra.CustomConnectionPool;

/**
 * очищает данные в таблице в соответствии с постановкой
 * задачи - по дате и наименованию колонки.
 */
@Slf4j
public final class JdbcTruncOlderThanImpl implements TruncOlderThanExecr {

    /**
     * сообщение ошибки.
     */
    private static final String SQL_ERR_MSG = "sql error: %s";

    @Override
    public void truncateRecords(
            final String tableName,
            final String colName,
            final LocalDateTime dateFrom) {
        try (var connection = CustomConnectionPool.getConnection()) {
            // TODO
            log.info("nop");
        } catch (SQLException e) {
            final var msg = String.format(SQL_ERR_MSG, e.getMessage());
            log.error(msg);
            throw new CustomSQLException(msg);
        }
    }

    /**
     * проверяет является ли переданная колонка форматом даты
     * если не дата то название колонки считаем не корректным.
     * исключение если колонка не существует или она не формата даты
     *
     * @param connection
     * @param tableName
     * @param colName
     */
    private void checkColFomat(
            final Connection connection,
            final String tableName,
            final String colName)
            throws SQLException {
        // TODO
    }

}
