package ru.mentor.exception;

import lombok.Getter;

/**
 * Исключение, возникающее при ошибке повторных попыток выполнения gRPC вызова.
 * Расширяет RuntimeException и содержит дополнительную информацию об идентификаторе запроса.
 */
@Getter
public class GrpcRetryException extends RuntimeException {

    /**
     * Идентификатор запроса, связанный с исключением.
     */
    private String requestId = null;

    /**
     * Создает новое исключение GrpcRetryException без сообщения и идентификатора запроса.
     */
    public GrpcRetryException() {
        super();
    }

    /**
     * Создает новое исключение GrpcRetryException с указанным сообщением.
     *
     * @param message сообщение об ошибке
     */
    public GrpcRetryException(String message) {
        super(message);
    }

    /**
     * Создает новое исключение GrpcRetryException с указанным сообщением и идентификатором запроса.
     *
     * @param message сообщение об ошибке
     * @param requestId идентификатор запроса
     */
    public GrpcRetryException(String message, String requestId) {
        super(message);
        this.requestId = requestId;
    }

}