package ru.ssp.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import ru.ssp.exceptions.CustomConcurrencyException;
import ru.ssp.jdbc.JdbcTruncOlderThanImpl;

/**
 * обеспечивает конкурентную работу.
 * и фабричный метод создания нужной имплементации
 * интерфейса удаления - создает и передает ему управление
 */
class ConcurrentTruncOlderThanExecr
        implements TruncOlderThanExecr, JdbcTruncOlderThanCreateAware {

    /**
     * сообщение при формировании исключения.
     */
    private static final String ANOTHER_THREAD_BLOCKING_TABLE =

            "another thread blocks table";

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
        final var lock = LOCKS
                .computeIfAbsent(tableName, k -> new ReentrantLock());
        if (lock.tryLock()) {
            try {
                this.create().truncateRecords(tableName, colName, dateFrom);
            } finally {
                lock.unlock();
            }
        } else {
            throw new CustomConcurrencyException(ANOTHER_THREAD_BLOCKING_TABLE);
        }
    }

    @Override
    public TruncOlderThanExecr create() {
        return new JdbcTruncOlderThanImpl();
    }

}
