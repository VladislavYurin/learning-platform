package ru.mentor.exception;

import lombok.Getter;

@Getter
public class AccessDeniedException extends RuntimeException {


    private String rqUId = null;

    public AccessDeniedException() {
        super();
    }

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }
}
