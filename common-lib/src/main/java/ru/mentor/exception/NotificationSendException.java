package ru.mentor.exception;

import lombok.Getter;

@Getter
public class NotificationSendException extends RuntimeException {
    private String requestId = null;

    public NotificationSendException() {
        super();
    }

    public NotificationSendException(String message) {
        super(message);
    }

    public NotificationSendException(String message, String requestId) {
        super(message);
        this.requestId = requestId;
    }
}
