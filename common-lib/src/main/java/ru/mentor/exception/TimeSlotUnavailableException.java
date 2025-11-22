package ru.mentor.exception;

import lombok.Getter;

@Getter
public class TimeSlotUnavailableException extends RuntimeException {

    private String requestId = null;

    public TimeSlotUnavailableException() {
        super();
    }

    public TimeSlotUnavailableException(String message) {
        super(message);
    }

    public TimeSlotUnavailableException(String message, String requestId) {
        super(message);
        this.requestId = requestId;
    }
}