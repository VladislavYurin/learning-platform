package ru.mentor.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;
import ru.mentor.entity.UserCourseAccessEntity;

/**
 * Репозиторий хранит данные о выданных пользователям доступах к курсам
 */
public interface UserCourseAccessRepository
        extends ReactiveCrudRepository<UserCourseAccessEntity, Long> {

    /**
     * Возвращает true, если у пользователя с id == userId есть доступ к курсу c id == courseId
     *
     * @param userId - ID пользователя в таблице users
     * @param courseId - ID курса в таблице courses
     *
     * @return - Mono с true, если у пользователя есть доступ к курсу, или false, если нет
     */
    Mono<Boolean> existsByUserIdAndCourseId(Long userId, Long courseId);
}
