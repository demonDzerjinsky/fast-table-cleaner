package ru.ssp.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * обеспечивает конкурентную работу.
 * и фабричный метод создания нужной имплементации
 * интерфейса удаления - создает и передает ему управление
 */
class ConcurrentTruncOlderThanExecr
        implements TruncOlderThanExecr, JdbcTruncOlderThanCreateAware {

    /**
     * блокировки совместного доступа на таблицы.
     */
    private static final Map<String, ReentrantLock> LOCKS

            = new ConcurrentHashMap<>();

    @Override
    public void truncateRecords(
            final String tableName,
            final String colName,
            final LocalDateTime dateFrom) {

    }

    @Override
    public TruncOlderThanExecr create() {
        // TODO Auto-generated method stub
        return null;
    }

}
