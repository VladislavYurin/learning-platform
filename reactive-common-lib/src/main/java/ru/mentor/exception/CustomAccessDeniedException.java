package ru.mentor.exception;

import lombok.Getter;

/**
 * Пользовательское исключение, представляющее ошибку отказа в доступе.
 * Расширяет RuntimeException и содержит дополнительную информацию о идентификаторе запроса.
 */
@Getter
public class CustomAccessDeniedException extends RuntimeException {

    /**
     * Идентификатор запроса, связанный с исключением.
     * Может быть null, если не установлен.
     */
    private String requestId = null;

    /**
     * Создает новое исключение CustomAccessDeniedException без сообщения и идентификатора запроса.
     */
    public CustomAccessDeniedException() {
        super();
    }

    /**
     * Создает новое исключение CustomAccessDeniedException с указанным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public CustomAccessDeniedException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение CustomAccessDeniedException с указанным сообщением и идентификатором запроса.
     *
     * @param message сообщение об ошибке
     * @param requestId идентификатор запроса
     */
    public CustomAccessDeniedException(String message, String requestId) {
        super(message);
        this.requestId = requestId;
    }
}
