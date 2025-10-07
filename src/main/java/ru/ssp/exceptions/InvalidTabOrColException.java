package ru.ssp.exceptions;

/**
 * исключение в сл некорректно переданной таблицы и/или колонки.
 */
public class InvalidTabOrColException extends RuntimeException {

    /**
     * конструктор.
     *
     * @param msg
     */
    public InvalidTabOrColException(final String msg) {
        super(msg);
    }
}
