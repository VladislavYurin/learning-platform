package ru.mentor.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;
import ru.mentor.entity.UserModuleAccessEntity;


public interface UserModuleAccessRepository extends ReactiveCrudRepository<UserModuleAccessEntity, Long>,
        ReactiveSortingRepository<UserModuleAccessEntity, Long> {
    /**
     * Проверяет, существует ли доступ пользователя к указанному модулю.
     *
     * @param userId идентификатор пользователя
     * @param moduleId идентификатор модуля
     * @return true, если доступ существует, иначе false
     */
    Mono<Boolean> existsByUserIdAndModuleId(Long userId, Long moduleId);

    Mono<Boolean> deleteAllByUserIdAndCourseId(Long userId, Long moduleId);
}
