package ru.mentor.exception;

public class FileProcessingException extends RuntimeException {

    public FileProcessingException() {
        super();
    }

    public FileProcessingException(String message) {
        super(message);
    }

}
