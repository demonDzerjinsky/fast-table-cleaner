package ru.ssp.dto;

import java.time.LocalDateTime;

/**
 * контракт вызова удаления из таблицы данных.
 *
 * @param tableName наименование таблицы
 * @param tableCol  наименование колонки
 * @param dateFrom  параметр начиная с какой даты будет очистка данных
 */
public record TruncateOlderThanRequestDto(
        String tableName,
        String tableCol,
        LocalDateTime dateFrom) {
}
