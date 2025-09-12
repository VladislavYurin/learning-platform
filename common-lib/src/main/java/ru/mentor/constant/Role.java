package ru.mentor.constant;

import ru.mentor.entity.CourseEntity;
import ru.mentor.entity.UserEntity;
import ru.mentor.exception.CustomAccessDeniedException;

/**
 * Перечисление ролей пользователей в системе.
 * Представляет различные уровни доступа пользователей в приложении.
 */
public enum Role {
    /** Роль обычного пользователя */
    USER,
    /** Роль администратора системы */
    ADMIN,
    /** Роль ментора */
    MENTOR;

    /**
     * Проверяет, является ли пользователь администратором.
     *
     * @param user объект пользователя для проверки
     * @return true, если пользователь имеет роль ADMIN, иначе false
     */
    public static Boolean checkIsAdmin(UserEntity user) {
        return user.getRole().equals(Role.ADMIN);
    }

    /**
     * Проверяет, является ли пользователь ментором.
     *
     * @param user объект пользователя для проверки
     * @return true, если пользователь имеет роль MENTOR, иначе false
     */
    public static Boolean checkIsMentor(UserEntity user) {
        return user.getRole().equals(Role.MENTOR);
    }

    /**
     * Проверяет, является ли пользователь автором указанного курса.
     *
     * @param user   объект пользователя для проверки
     * @param course объект курса для проверки авторства
     * @return true, если пользователь является автором курса, иначе false
     */
    public static Boolean checkMentorIsAuthorOfCourse(UserEntity user, CourseEntity course) {
        return course.getAuthor().equals(user);
    }

}
