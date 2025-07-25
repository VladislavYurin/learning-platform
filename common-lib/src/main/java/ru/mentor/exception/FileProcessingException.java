package ru.mentor.exception;

import lombok.Getter;

@Getter
public class FileProcessingException extends RuntimeException {


    private String rqUId = null;

    public FileProcessingException() {
        super();
    }

    public FileProcessingException(String message) {
        super(message);
    }

    public FileProcessingException(String message, String rqUId) {
        super(message);
        this.rqUId = rqUId;
    }

}
