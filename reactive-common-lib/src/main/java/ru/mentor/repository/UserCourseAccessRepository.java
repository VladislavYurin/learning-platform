package ru.mentor.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mentor.entity.UserCourseAccessEntity;

/**
 * Репозиторий для работы с сущностями доступа пользователей к курсам.
 * Предоставляет методы для выполнения CRUD операций и дополнительные методы
 * для управления доступом пользователей к курсам.
 */
@Repository
public interface UserCourseAccessRepository extends
        ReactiveCrudRepository<UserCourseAccessEntity, Long> {

    /**
     * Проверяет, существует ли доступ пользователя к указанному курсу.
     *
     * @param userId
     *         идентификатор пользователя
     * @param courseId
     *         идентификатор курса
     *
     * @return true, если доступ существует, иначе false
     */
    Mono<Boolean> existsByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Находит все записи доступа для указанного пользователя.
     *
     * @param userId
     *         идентификатор пользователя
     *
     * @return список всех записей доступа пользователя
     */
    Flux<UserCourseAccessEntity> findAllByUserId(Long userId);

    /**
     * Находит запись доступа пользователя к указанному курсу.
     *
     * @param userId
     *         идентификатор пользователя
     * @param courseId
     *         идентификатор курса
     *
     * @return сущность доступа пользователя к курсу или null, если доступ не найден
     */
    Mono<UserCourseAccessEntity> findByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Удаляет запись доступа пользователя к указанному курсу.
     *
     * @param userId
     *         идентификатор пользователя
     * @param courseId
     *         идентификатор курса
     */
    Mono<Void> deleteByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Находит все записи доступа к указанному курсу.
     *
     * @param courseId
     *         идентификатор курса
     *
     * @return список всех записей доступа к курсу
     */
    Flux<UserCourseAccessEntity> findAllByCourseId(Long courseId);

}
