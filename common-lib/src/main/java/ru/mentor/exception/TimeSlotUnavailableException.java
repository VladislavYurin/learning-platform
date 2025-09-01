package ru.mentor.exception;

import lombok.Getter;

@Getter
public class TimeSlotUnavailableException extends RuntimeException {

    private String rqUId = null;

    public TimeSlotUnavailableException() {
        super();
    }

    public TimeSlotUnavailableException(String message) {
        super(message);
    }

    public TimeSlotUnavailableException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }
}