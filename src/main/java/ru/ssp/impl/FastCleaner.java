package ru.ssp.impl;

import static ru.ssp.dto.TruncOlderThanResponse.Status.FAIL;
import static ru.ssp.dto.TruncOlderThanResponse.Status.OK;

import lombok.extern.slf4j.Slf4j;
import ru.ssp.dto.TruncOlderThanRequest;
import ru.ssp.dto.TruncOlderThanResponse;

/**
 * реализует API очистки старых строк в соответствии с контрактом.
 */
@Slf4j
public final class FastCleaner {

    /**
     * сообщение в лог.
     */
    private static final String STATUS_FAIL_MESSAGE =

            "Status = FAIL, message = {}";

    /**
     * валидатор входного контракта.
     */
    private final Validator<TruncOlderThanRequest, TruncOlderThanResponse> vl;

    /**
     * выполняет задачу очистки.
     */
    private final TruncOlderThanExecr te;

    private FastCleaner(
            final Validator<TruncOlderThanRequest, TruncOlderThanResponse> v,
            final TruncOlderThanExecr ex) {
        this.vl = v;
        this.te = ex;
    }

    /**
     * входной метод реализующий API.
     *
     * @param request входной контракт вызова api
     * @return результат выполнения
     */
    public static TruncOlderThanResponse truncate(
            final TruncOlderThanRequest request) {
        return createInstance().doTruncate(request);
    }

    /**
     * порождающий метод.
     *
     * @return экземпляр правильно собранного класса
     */
    private static FastCleaner createInstance() {
        return null; // todo
    }

    /**
     * реализует логику операции очистки.
     * мапинг входного контракта, вызов требуемых компонент,
     * обратный мапинг в контракт ответа.
     *
     * @param rq запрос в контракте
     * @return ответ по результату выполнения в контракте
     */
    private TruncOlderThanResponse doTruncate(
            final TruncOlderThanRequest rq) {
        return vl.validate(rq)
                .orElseGet(() -> {
                    try {
                        te.truncateRecords(rq.tableName(),
                                rq.tableCol(),
                                rq.dateFrom());
                        return new TruncOlderThanResponse(OK, null);
                    } catch (Exception e) {
                        log.error(STATUS_FAIL_MESSAGE, e);
                        return new TruncOlderThanResponse(FAIL, e.getMessage());
                    }
                });
    }
}
