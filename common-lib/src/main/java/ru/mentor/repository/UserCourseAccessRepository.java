package ru.mentor.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.UserCourseAccessEntity;

/**
 * Репозиторий для работы с сущностями доступа пользователей к курсам.
 * Предоставляет методы для выполнения CRUD операций и дополнительные методы
 * для управления доступом пользователей к курсам.
 */
@Repository
public interface UserCourseAccessRepository extends JpaRepository<UserCourseAccessEntity, Long> {

    /**
     * Проверяет, существует ли доступ пользователя к указанному курсу.
     *
     * @param userId идентификатор пользователя
     * @param courseId идентификатор курса
     * @return true, если доступ существует, иначе false
     */
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Находит все записи доступа для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список всех записей доступа пользователя
     */
    List<UserCourseAccessEntity> findAllByUserId(Long userId);

    /**
     * Находит запись доступа пользователя к указанному курсу.
     *
     * @param userId идентификатор пользователя
     * @param courseId идентификатор курса
     * @return сущность доступа пользователя к курсу или null, если доступ не найден
     */
    Optional<UserCourseAccessEntity> findByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Удаляет запись доступа пользователя к указанному курсу.
     *
     * @param userId идентификатор пользователя
     * @param courseId идентификатор курса
     */
    void deleteByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Находит все записи доступа к указанному курсу.
     *
     * @param courseId идентификатор курса
     * @return список всех записей доступа к курсу
     */
    List<UserCourseAccessEntity> findAllByCourseId(Long courseId);

}
