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
    private String requestId = null;

    /**
     * Создает новое исключение FileProcessingException без сообщения и идентификатора запроса.
     */
    public FileProcessingException() {
        super();
    }

    /**
     * Создает новое исключение FileProcessingException с указанным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public FileProcessingException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение FileProcessingException с указанным сообщением и идентификатором запроса.
     *
     * @param message сообщение об ошибке
     * @param requestId идентификатор запроса
     */
    public FileProcessingException(String message, String requestId) {
        super(message);
        this.requestId = requestId;
    }

}
