package ru.mentor.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private String rqUId = null;

    public UserException() {
        super();
    }

    public UserException(String message) {
        super(message);
    }

    public UserException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }

}
