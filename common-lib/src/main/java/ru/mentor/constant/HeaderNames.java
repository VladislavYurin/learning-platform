package ru.mentor.constant;

/**
 * Имена HTTP-заголовков, используемых для передачи
 * служебной информации между микросервисами.
 */
public class HeaderNames {

    /**
     * Заголовок, содержащий идентификатор запроса
     * для межсервисной корреляции.
     */
    public static final String REQUEST_ID = "requestId";

    private HeaderNames() {}
}
