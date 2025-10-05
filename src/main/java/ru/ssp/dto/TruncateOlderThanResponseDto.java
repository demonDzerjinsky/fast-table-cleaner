package ru.ssp.dto;

/**
 * контракт ответа.
 *
 * @param status  статус выполнения
 * @param message сообщение в сл ошибки
 */
public record TruncateOlderThanResponseDto(Status status, String message) {
    public enum Status {
        /**
         * успешное завершение.
         */
        OK,
        /**
         * ошибка выполнения.
         */
        FAIL
    }
}
