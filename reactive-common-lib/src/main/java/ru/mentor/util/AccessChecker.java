package ru.mentor.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import ru.mentor.constant.Role;
import ru.mentor.repository.CourseRepository;
import ru.mentor.repository.UserCourseAccessRepository;
import ru.mentor.repository.UserModuleAccessRepository;
import ru.mentor.repository.UserRepository;

/**
 * Компонент для проверки прав пользователя на доступ к курсам и к модулям курсов
 */
@Component
@RequiredArgsConstructor
public class AccessChecker {

    private final UserCourseAccessRepository userCourseAccessRepository;
    private final UserModuleAccessRepository userModuleAccessRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    /**
     * Проверяет, имеет ли пользователь право доступа к курсу на основании
     * записи в репозитории {@link UserCourseAccessRepository}
     *
     * @param userId - ID пользователя
     * @param courseId - ID курса
     *
     * @return - Mono с Boolean значением true, если пользователь имеет доступ
     */
    public Mono<Boolean> hasAccessToCourse(Long userId, Long courseId) {
        return userCourseAccessRepository.existsByUserIdAndCourseId(userId, courseId);
    }

    /**
     * Проверяет, имеет ли пользователь право доступа к модулю курса
     *
     * @param userId - ID пользователя
     * @param courseId - ID курса
     * @param moduleOrderNumber - порядковый номер модуля курса
     * @return - Mono c  Boolean значением true, если пользователь имеет доступ к модулю
     */
    public Mono<Boolean> hasAccessToModule(Long userId, Long courseId, Integer moduleOrderNumber) {
        return userRepository
            .findByIdOrThrow(userId)
            .flatMap(user ->
                courseRepository
                    .findByIdOrThrow(courseId)
                    .flatMap(course ->
                        userModuleAccessRepository
                            .existsByUserIdAndCourseIdAndModuleOrderNum(
                                userId, courseId, moduleOrderNumber)
                            .map(hasAccess ->
                                             (hasAccess || (Role.checkIsMentor(user) && course.getAuthorId().equals(userId)))
                            )
                    )
            );
    }

    /**
     * Проверяет, является ли пользователь автором курса
     *
     * @param userId - ID пользователя
     * @param courseId - ID курса
     *
     * @return Mono с Boolean значением true, если пользователь является автором курса
     */
    public Mono<Boolean> isCourseAuthor(Long userId, Long courseId) {
        return userRepository
            .findByIdOrThrow(userId)
            .flatMap(user ->
                courseRepository
                    .findByIdOrThrow(courseId)
                    .map(course ->
                        Role.checkIsMentor(user) && user.getId().equals(course.getAuthorId())
                    )
            );
    }

}
