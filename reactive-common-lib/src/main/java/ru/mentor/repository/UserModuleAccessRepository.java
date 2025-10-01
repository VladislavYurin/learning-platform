package ru.mentor.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mentor.entity.UserModuleAccessEntity;

/**
 * Репозиторий для работы с сущностями доступа пользователей к модулям.
 * Предоставляет методы для выполнения CRUD операций и дополнительные методы
 * для управления доступом пользователей к модулям курсов.
 */
@Repository
public interface UserModuleAccessRepository extends
        ReactiveCrudRepository<UserModuleAccessEntity, Long> {

    /**
     * Проверяет, существует ли доступ пользователя к указанному модулю.
     *
     * @param userId
     *         идентификатор пользователя
     * @param moduleId
     *         идентификатор модуля
     *
     * @return true, если доступ существует, иначе false
     */
    Mono<Boolean> existsByUserIdAndModuleId(Long userId, Long moduleId);

    /**
     * Удаляет все записи доступа пользователя к модулям указанного курса.
     *
     * @param userId
     *         идентификатор пользователя
     * @param courseId
     *         идентификатор курса
     */
    Mono<Void> deleteAllByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Удаляет запись доступа пользователя к указанному модулю.
     *
     * @param userId
     *         идентификатор пользователя
     * @param moduleId
     *         идентификатор модуля
     */
    Mono<Void> deleteByUserIdAndModuleId(Long userId, Long moduleId);

    /**
     * Находит все записи доступа к модулям указанного курса.
     *
     * @param courseId
     *         идентификатор курса
     *
     * @return список всех записей доступа к модулям курса
     */
    Flux<UserModuleAccessEntity> findAllByCourseId(Long courseId);

    /**
     * Находит последнюю запись доступа пользователя к модулям указанного курса,
     * отсортированную по дате предоставления доступа в порядке убывания.
     *
     * @param id
     *         идентификатор пользователя
     * @param courseId
     *         идентификатор курса
     *
     * @return Optional с последней записью доступа или пустой Optional, если доступ не найден
     */
    Mono<UserModuleAccessEntity> findTopByUserIdAndCourseIdOrderByAccessGrantedAtDesc(
            Long id,
            Long courseId);

    /**
     * Находит все записи доступа пользователя к модулям указанного курса.
     *
     * @param id
     *         идентификатор пользователя
     * @param courseId
     *         идентификатор курса
     *
     * @return список всех записей доступа пользователя к модулям курса
     */
    Flux<UserModuleAccessEntity> findAllByUserIdAndCourseId(Long id, Long courseId);

}