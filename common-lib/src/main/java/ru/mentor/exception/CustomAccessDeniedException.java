package ru.mentor.exception;

import lombok.Getter;

/**
 * Пользовательское исключение, представляющее ошибку отказа в доступе.
 * Расширяет RuntimeException и содержит дополнительную информацию об идентификаторе запроса.
 */
@Getter
public class CustomAccessDeniedException extends RuntimeException {

    /**
     * Идентификатор запроса, связанный с исключением.
     * Может быть null, если не установлен.
     */
    private String rqUId = null;

    /**
     * Создает новое исключение CustomAccessDeniedException без сообщения и идентификатора запроса.
     */
    public CustomAccessDeniedException() {
        super();
    }

    /**
     * Создает новое исключение CustomAccessDeniedException с указанным сообщением.
     */
    public CustomAccessDeniedException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение CustomAccessDeniedException с указанным сообщением и идентификатором запроса.
     */
    public CustomAccessDeniedException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }
}
