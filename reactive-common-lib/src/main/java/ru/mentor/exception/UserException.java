package ru.mentor.exception;

import lombok.Getter;

/**
 * Исключение, представляющее ошибки, связанные с пользовательскими операциями.
 * Расширяет RuntimeException и содержит дополнительную информацию об идентификаторе запроса.
 */
@Getter
public class UserException extends RuntimeException {

    /**
     * Идентификатор запроса, связанный с исключением.
     */
    private String rqUId = null;

    /**
     * Создает новое исключение UserException без сообщения и идентификатора запроса.
     */
    public UserException() {
        super();
    }

    /**
     * Создает новое исключение UserException с указанным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public UserException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение UserException с указанным сообщением и идентификатором запроса.
     *
     * @param message сообщение об ошибке
     * @param rqUId идентификатор запроса
     */
    public UserException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }

}
