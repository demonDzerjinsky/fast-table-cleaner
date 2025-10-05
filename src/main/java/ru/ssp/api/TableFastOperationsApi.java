package ru.ssp.api;

import ru.ssp.dto.TruncateOlderThanRequestDto;
import ru.ssp.dto.TruncateOlderThanResponseDto;

/**
 * входная точка вызова API операций с таблицами.
 * реализован метод быстрой очистки в соответствии
 * с постановкой задачи.
 */
public final class TableFastOperationsApi {
    private TableFastOperationsApi() {
    }

    /**
     * выполняет очистку данных по заданной таблице, колонке и дате.
     *
     * @param request контракт вызова
     * @return контракт ответа
     */
    public static TruncateOlderThanResponseDto truncateOlderThan(
            final TruncateOlderThanRequestDto request) {
        // TODO
        return null;
    }
}
