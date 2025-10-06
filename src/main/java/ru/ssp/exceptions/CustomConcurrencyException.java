package ru.ssp.exceptions;

/**
 * исключение при блокировке совместного доступа.
 */
public class CustomConcurrencyException extends RuntimeException {

    /**
     * конструктор.
     *
     * @param msg сообщение
     */
    public CustomConcurrencyException(final String msg) {
        super(msg);
    }
}
