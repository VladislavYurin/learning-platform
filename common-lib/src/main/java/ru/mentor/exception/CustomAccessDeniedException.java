package ru.mentor.exception;

import lombok.Getter;

@Getter
public class CustomAccessDeniedException extends RuntimeException {


    private String rqUId = null;

    public CustomAccessDeniedException() {
        super();
    }

    public CustomAccessDeniedException(String message) {
        super(message);
    }

    public CustomAccessDeniedException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }
}
