package ru.mentor.exception.useravatar;

public class UserAvatarValidationException extends UserAvatarException {

    public UserAvatarValidationException(String message) {
        super(message);
    }

    public UserAvatarValidationException(String message, Throwable cause) {
        super(message, cause);
    }

}
