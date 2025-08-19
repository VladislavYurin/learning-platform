package ru.mentor.constant;

import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.CustomAccessDeniedException;

/**
 * Перечисление ролей пользователей в системе.
 * Представляет различные уровни доступа пользователей в приложении.
 */
public enum Role {
    USER,
    ADMIN,
    MENTOR;

    /**
     * Проверяет, является ли пользователь администратором.
     */
    public static Boolean checkIsAdmin(UserEntity user) {
        return user.getRole().equals(Role.ADMIN);
    }

    /**
     * Проверяет, является ли пользователь ментором.
     */
    public static Boolean checkIsMentor(UserEntity user) {
        return user.getRole().equals(Role.MENTOR);
    }

    /**
     * Проверяет, является ли пользователь автором указанного курса.
     */
    public static Boolean checkMentorIsAuthorOfCourse(UserEntity user, CourseEntity course) {
        return course.getAuthor().equals(user);
    }

    /**
     * Проверяет, является ли пользователь администратором или ментором.
     */
    public static void checkUserIsAdminOrMentor(UserEntity user) {
        if (!checkIsAdmin(user) && !checkIsMentor(user)) {
            throw new CustomAccessDeniedException(String.format(
                    "Юзер с ID = %d не имеет доступа к выдаче доступа к курсам и модулям",
                    user.getId()
            ));
        }
    }

}
