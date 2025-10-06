package ru.ssp.infra;

import java.util.Properties;

/**
 * настройки окружения.
 */
public final class CustomProperties {

    /**
     * наименование файла с настройками для либы.
     */
    private static final String APPLICATION_PROPERTIES

            = "application.properties";

    /**
     * карта свойств.
     */
    private static final Properties PTYS = new Properties();

    static {
        load();
    }

    private CustomProperties() {
    }

    private static void load() {
        try (var ptyStream = CustomProperties.class
                .getClassLoader()
                .getResourceAsStream(APPLICATION_PROPERTIES)) {
            PTYS.load(ptyStream);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * получает значение свойства по наименованию свойства.
     *
     * @param ptyName наименование свойства
     * @return значение свойства
     */
    public static String getPty(final String ptyName) {
        return PTYS.getProperty(ptyName);
    }
}
