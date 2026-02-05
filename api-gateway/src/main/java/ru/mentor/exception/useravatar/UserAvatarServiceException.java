package ru.mentor.exception.useravatar;

public class UserAvatarServiceException extends UserAvatarException {

    public UserAvatarServiceException(String message) {
        super(message);
    }

    public UserAvatarServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
