package ru.mentor.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;
import ru.mentor.entity.UserCourseAccessEntity;

public interface UserCourseAccessRepository extends ReactiveCrudRepository<UserCourseAccessEntity, Long>,
        ReactiveSortingRepository<UserCourseAccessEntity, Long> {

    /**
     * Проверяет, существует ли доступ пользователя к указанному курсу.
     *
     * @param userId идентификатор пользователя
     * @param courseId идентификатор курса
     * @return true, если доступ существует, иначе false
     */
    Mono<Boolean> existsByUserIdAndCourseId(Long userId, Long courseId);


    Mono<Boolean> deleteByUserIdAndCourseId(Long userId, Long courseId);
}