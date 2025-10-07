package ru.ssp.exceptions;

/**
 * исключение при ошибке выполнения sql-запросов.
 */
public class CustomSQLException extends RuntimeException {

    /**
     * конструктор.
     *
     * @param msg
     */
    public CustomSQLException(final String msg) {
        super(msg);
    }

    /**
     * конструктор.
     *
     * @param e
     */
    public CustomSQLException(final Throwable e) {
        super(e);
    }
}
