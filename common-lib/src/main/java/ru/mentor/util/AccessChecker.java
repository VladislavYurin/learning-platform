package ru.mentor.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.mentor.repository.UserCourseAccessRepository;
import ru.mentor.repository.UserModuleAccessRepository;

/**
 * Утилитарный класс для проверки доступа пользователей к курсам и модулям.
 * Предоставляет методы для проверки наличия доступа на основе записей в репозиториях.
 */
@Component
@RequiredArgsConstructor
public class AccessChecker {

    /**
     * Репозиторий для работы с доступом пользователей к курсам.
     */
    private final UserCourseAccessRepository userCourseAccessRepository;

    /**
     * Репозиторий для работы с доступом пользователей к модулям.
     */
    private final UserModuleAccessRepository userModuleAccessRepository;

    /**
     * Проверяет, имеет ли пользователь доступ к указанному курсу.
     *
     * @param userId идентификатор пользователя
     * @param courseId идентификатор курса
     * @return true, если пользователь имеет доступ к курсу, иначе false
     */
    public boolean hasAccessToCourse(Long userId, Long courseId) {
        return userCourseAccessRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    /**
     * Проверяет, имеет ли пользователь доступ к указанному модулю.
     *
     * @param userId идентификатор пользователя
     * @param moduleId идентификатор модуля
     * @return true, если пользователь имеет доступ к модулю, иначе false
     */
    public boolean hasAccessToModule(Long userId, Long moduleId) {
        return userModuleAccessRepository.existsByUserIdAndModuleId(userId, moduleId);
    }

}
