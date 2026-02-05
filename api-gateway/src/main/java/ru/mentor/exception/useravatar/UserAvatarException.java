package ru.mentor.exception.useravatar;

public class UserAvatarException extends RuntimeException {
    public UserAvatarException(String message) {
        super(message);
    }

    public UserAvatarException(String message, Throwable cause) {
        super(message, cause);
    }
}
