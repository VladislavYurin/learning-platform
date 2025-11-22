package ru.mentor.exception;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private String requestId = null;

    public ConflictException() {
        super();
    }

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, String requestId) {
        super(message);
        this.requestId = requestId;
    }
}
