package ru.ssp.api;

import static ru.ssp.impl.FastCleaner.truncate;

import ru.ssp.dto.TruncOlderThanRequest;
import ru.ssp.dto.TruncOlderThanResponse;

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
    public static TruncOlderThanResponse truncateOlderThan(
            final TruncOlderThanRequest request) {
        return truncate(request);
    }
}
