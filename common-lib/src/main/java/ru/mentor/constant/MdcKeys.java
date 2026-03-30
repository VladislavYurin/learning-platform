package ru.mentor.constant;

/**
 * Ключи для MDC.
 * Используются для передачи контекстной информации в логирование.
 */
public class MdcKeys {

    /**
     * Ключ идентификатора запроса для корреляции логов.
     */
    public static final String REQUEST_ID = "requestId";

    /**
     * Ключ имени сервиса, записываемого в MDC.
     */
    public static final String SERVICE_NAME = "serviceName";

    /**
     * Ключ имени пользователя, если пользователь
     * был успешно аутентифицирован.
     */
    public static final String USERNAME = "username";
}
