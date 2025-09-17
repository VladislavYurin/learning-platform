package ru.mentor.exception;

import lombok.Getter;

@Getter
public class NotificationSendException extends RuntimeException {
    private String rqUId = null;

    public NotificationSendException() {
        super();
    }

    public NotificationSendException(String message) {
        super(message);
    }

    public NotificationSendException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }
}
