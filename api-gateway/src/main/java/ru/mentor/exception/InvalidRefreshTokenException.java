package ru.mentor.exception;

/**
 * Исключение, выбрасываемое при передаче невалидного refresh-токена.
 */
public class InvalidRefreshTokenException extends RuntimeException {

    /**
     * Создаёт исключение с сообщением об ошибке.
     *
     * @param message
     *         текст ошибки
     */
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}