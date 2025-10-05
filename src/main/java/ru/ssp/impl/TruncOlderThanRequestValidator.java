package ru.ssp.impl;

import static ru.ssp.dto.TruncOlderThanResponse.Status.FAIL;

import java.util.Optional;

import ru.ssp.dto.TruncOlderThanRequest;
import ru.ssp.dto.TruncOlderThanResponse;

/**
 * валидирует входной контракт вызова api TruncOlderThanRequest.
 */
class TruncOlderThanRequestValidator
        implements Validator<TruncOlderThanRequest, TruncOlderThanResponse> {

    /**
     * сообщение в сл ошибки валидации.
     */
    private static final String CONTR_VALID_ERR = "";

    @Override
    public Optional<TruncOlderThanResponse> validate(
            final TruncOlderThanRequest req) {
        return Optional.of(req).filter(this::checkIfErr)
                .map(p -> new TruncOlderThanResponse(FAIL, CONTR_VALID_ERR));
    }

    private boolean checkIfErr(final TruncOlderThanRequest req) {
        return req.tableName() == null
                || req.tableCol() == null
                || req.dateFrom() == null
                || req.tableName().isBlank()
                || req.tableCol().isBlank();
    }
}
