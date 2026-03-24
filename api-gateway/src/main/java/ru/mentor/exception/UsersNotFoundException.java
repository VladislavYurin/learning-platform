package ru.mentor.exception;


/**
 * Исключение когда пользователи не найдены
 */
public class UsersNotFoundException extends RuntimeException {

    public UsersNotFoundException(String message) {
        super(message);
    }
}
