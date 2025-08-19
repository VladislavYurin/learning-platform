package ru.mentor.exception;

import lombok.Getter;

/**
 * Исключение, возникающее при ошибке обработки файла.
 * Расширяет RuntimeException и содержит дополнительную информацию об идентификаторе запроса.
 */
@Getter
public class FileProcessingException extends RuntimeException {

    /**
     * Идентификатор запроса, связанный с исключением.
     */
    private String rqUId = null;

    /**
     * Создает новое исключение FileProcessingException без сообщения и идентификатора запроса.
     */
    public FileProcessingException() {
        super();
    }

    /**
     * Создает новое исключение FileProcessingException с указанным сообщением.
     */
    public FileProcessingException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение FileProcessingException с указанным сообщением и идентификатором запроса.
     */
    public FileProcessingException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }

}
