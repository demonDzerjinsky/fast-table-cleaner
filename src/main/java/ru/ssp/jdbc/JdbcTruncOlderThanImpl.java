package ru.ssp.jdbc;

import java.time.LocalDateTime;

import ru.ssp.impl.TruncOlderThanExecr;

/**
 * очищает данные в таблице в соответствии с постановкой
 * задачи - по дате и наименованию колонки.
 */
public class JdbcTruncOlderThanImpl implements TruncOlderThanExecr {

    @Override
    public void truncateRecords(
            final String tableName,
            final String colName,
            final LocalDateTime dateFrom) {
        // TODO Auto-generated method stub

    }
}
