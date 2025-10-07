package ru.ssp.jdbc;

import java.sql.SQLException;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import ru.ssp.exceptions.CustomSQLException;
import ru.ssp.exceptions.InvalidTabOrColException;
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

    /**
     * сообщение ошибки в переданных параметрах
     * таблицы или колонки таблицы.
     */
    private static final String NOT_FOUND_MSG = "tab or col not found";

    /**
     * сообщение ошибки в переданных параметрах
     * в сл если передана колонка не формата timestamp.
     */
    private static final String INVALID_COL_TYPE = "col not timestamp format";

    @Override
    public void truncateRecords(
            final String tableName,
            final String colName,
            final LocalDateTime dateFrom) {
        final String tabPostfix = "__fastlib";
        final String tempTableName = tableName + tabPostfix;
        checkColFomat(tableName, colName);
        renameTab(tableName, tempTableName); // меняем имя таблицы на временное
        boolean needRemoveRenamed = false;
        try {
            // переносим в таблицу с исходным именем только нужные данные.
            // остальная история останется в переименованной таблице и
            // в последствии удалится без транзакции вместе с таблицей.
            createTabAsSelect(tableName, tempTableName);
            needRemoveRenamed = true;
        } catch (Exception e) {
            // компенсирующее действие если чтото не так
            // возвращаем таблицу со старыми данными
            log.error("{}", e);
            try {
                renameTab(tempTableName, tableName);
                needRemoveRenamed = true;
            } catch (Exception ee) {
                log.error("{}", e);
                throw e;
            }
            throw e;
        } finally {
            if (needRemoveRenamed) {
                removeRenamedTab(tempTableName);
            }
        }
    }

    private void removeRenamedTab(final String tabName) {
    }

    private void renameTabReverse(final String tabName) {
    }

    private void renameTab(final String srcName, final String dstName) {

    }

    private void createTabAsSelect(
            final String createTable,
            final String srcTable) {

    }

    /**
     * проверяет является ли переданная колонка форматом даты
     * если не дата то название колонки считаем не корректным.
     * исключение если колонка не существует или она не формата даты
     *
     * @param tableName
     * @param colName
     */
    private void checkColFomat(
            final String tableName,
            final String colName) {
        try (var connection = CustomConnectionPool.getConnection()) {
            var meta = connection.getMetaData();
            var colsrs = meta.getColumns(connection.getCatalog(),
                    connection.getSchema(),
                    tableName, colName);
            if (colsrs.next()) {
                var actualColTypeName = colsrs.getString("TYPE_NAME");
                if (actualColTypeName.equals("timestamp")) {
                    return;
                } else {
                    throw new InvalidTabOrColException(INVALID_COL_TYPE);
                }
            } else {
                throw new InvalidTabOrColException(NOT_FOUND_MSG);
            }
        } catch (SQLException e) {
            final var msg = String.format(SQL_ERR_MSG, e.getMessage());
            log.error(msg);
            throw new CustomSQLException(msg);
        }
    }

}
