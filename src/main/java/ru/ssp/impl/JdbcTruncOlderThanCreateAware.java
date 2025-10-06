package ru.ssp.impl;

/**
 * обладатель интерфейса отвечает за корректное создание
 * нужной имплеметнации класса ответственного за очистку
 * данных из таблицы на уровне jdbc.
 */
public interface JdbcTruncOlderThanCreateAware
        extends BuildAware<TruncOlderThanExecr> {
}
