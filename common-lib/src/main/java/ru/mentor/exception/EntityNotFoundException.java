package ru.mentor.exception;

import lombok.Getter;

/**
 * Исключение, возникающее при попытке доступа к сущности, которая не существует в системе.
 * Расширяет RuntimeException и содержит дополнительную информацию об идентификаторе запроса.
 */
@Getter
public class EntityNotFoundException extends RuntimeException {

    /**
     * Идентификатор запроса, связанный с исключением.
     */
    private String rqUId = null;

    /**
     * Создает новое исключение EntityNotFoundException без сообщения и идентификатора запроса.
     */
    public EntityNotFoundException() {
        super();
    }

    /**
     * Создает новое исключение EntityNotFoundException с указанным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public EntityNotFoundException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение EntityNotFoundException с указанным сообщением и идентификатором запроса.
     *
     * @param message сообщение об ошибке
     * @param rqUId идентификатор запроса
     */
    public EntityNotFoundException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }

}
