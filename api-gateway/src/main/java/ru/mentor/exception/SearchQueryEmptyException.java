package ru.mentor.exception;

/**
 * Исключение для пустого поискового запроса
 */
public class SearchQueryEmptyException extends IllegalArgumentException {

    public SearchQueryEmptyException(String message) {
        super(message);
    }
}