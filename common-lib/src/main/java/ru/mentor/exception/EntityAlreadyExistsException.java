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
     */
    private String rqUId = null;

    /**
     * Создает новое исключение EntityAlreadyExistsException без сообщения и идентификатора запроса.
     */
    public EntityAlreadyExistsException() {
        super();
    }

    /**
     * Создает новое исключение EntityAlreadyExistsException с указанным сообщением.
     */
    public EntityAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение EntityAlreadyExistsException с указанным сообщением и идентификатором запроса.
     */
    public EntityAlreadyExistsException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }

}
