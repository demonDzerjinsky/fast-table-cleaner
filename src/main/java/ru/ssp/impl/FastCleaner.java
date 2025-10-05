package ru.ssp.impl;

import ru.ssp.dto.TruncOlderThanRequest;
import ru.ssp.dto.TruncOlderThanResponse;

/**
 * реализует API очистки старых строк в соответствии с контрактом.
 */
public final class FastCleaner {

    /**
     * валидатор входного контракта.
     */
    private final Validator<TruncOlderThanRequest, TruncOlderThanResponse> vl;

    /**
     * выполняет задачу очистки.
     */
    private final TruncOlderThanExecr truncateExecutor;

    private FastCleaner(
            final Validator<TruncOlderThanRequest, TruncOlderThanResponse> v,
            final TruncOlderThanExecr ex) {
        this.vl = v;
        this.truncateExecutor = ex;
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
     * @param request запрос в контракте
     * @return ответ по результату выполнения в контракте
     */
    private TruncOlderThanResponse doTruncate(
            final TruncOlderThanRequest request) {
        return null; // todo
    }
}
