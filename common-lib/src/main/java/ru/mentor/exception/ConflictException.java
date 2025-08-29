package ru.mentor.exception;

import lombok.Getter;

@Getter
public class ConflictException extends RuntimeException {

    private String rqUId = null;

    public ConflictException() {
        super();
    }

    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }
}
