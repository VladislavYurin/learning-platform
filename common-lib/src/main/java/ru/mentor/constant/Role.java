package ru.mentor.constant;

import ru.mentor.entity.UserEntity;

public enum Role {
    USER,
    ADMIN,
    MENTOR;

    public static Boolean checkIsAdmin(UserEntity user) {
        return user.getRole().equals(Role.ADMIN);
    }

    public static Boolean checkIsMentor(UserEntity user) {
        return user.getRole().equals(Role.MENTOR);
    }

}
