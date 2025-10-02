package ru.mentor.exception;

import lombok.Getter;

/**
 * Исключение, возникающее при попытке создания или сохранения сущности,
 * которая уже существует в системе.
 * Расширяет RuntimeException и содержит дополнительную информацию об идентификаторе запроса.
 */
@Getter
public class EntityAlreadyExistsException extends RuntimeException {

    /**
     * Идентификатор запроса, связанный с исключением.
     * Может быть null, если не установлен.
     */
    private String requestId = null;

    /**
     * Создает новое исключение EntityAlreadyExistsException без сообщения и идентификатора запроса.
     */
    public EntityAlreadyExistsException() {
        super();
    }

    /**
     * Создает новое исключение EntityAlreadyExistsException с указанным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public EntityAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение EntityAlreadyExistsException с указанным сообщением и идентификатором запроса.
     *
     * @param message сообщение об ошибке
     * @param requestId идентификатор запроса
     */
    public EntityAlreadyExistsException(String message, String requestId) {
        super(message);
        this.requestId = requestId;
    }

}
