package ru.ssp.impl;

import java.time.LocalDateTime;

/**
 * интерфейс очистки старых строк.
 */
interface TruncOlderThanExecr {

    /**
     * метод очистки старых строк.
     *
     * @param tableName наименование таблицы
     * @param colName   наименование колонки
     * @param dateFrom  параметр даты начала с которой очистка строк
     */
    void truncateRecords(
            String tableName,
            String colName,
            LocalDateTime dateFrom);
}
