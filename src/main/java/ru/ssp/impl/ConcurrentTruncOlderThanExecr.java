package ru.ssp.impl;

import java.time.LocalDateTime;

/**
 * обеспечивает конкурентную работу.
 */
class ConcurrentTruncOlderThanExecr implements TruncOlderThanExecr {

    @Override
    public void truncateRecords(
            final String tableName,
            final String colName,
            final LocalDateTime dateFrom) {

    }

}
