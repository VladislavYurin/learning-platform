package ru.mentor.exception;

import lombok.Getter;

@Getter
public class EntityNotFoundException extends RuntimeException {

    private String rqUId = null;

    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }

}
