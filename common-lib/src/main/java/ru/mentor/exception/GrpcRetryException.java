package ru.mentor.exception;

import lombok.Getter;

@Getter
public class GrpcRetryException extends RuntimeException {

    private String rqUId = null;

    public GrpcRetryException() {
        super();
    }

    public GrpcRetryException(String message) {
        super(message);
    }

    public GrpcRetryException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }

}