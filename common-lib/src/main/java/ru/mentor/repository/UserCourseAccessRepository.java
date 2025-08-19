package ru.mentor.repository;

import java.util.List;
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
     */
    boolean existsByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Находит все записи доступа для указанного пользователя.
     */
    List<UserCourseAccessEntity> findAllByUserId(Long userId);

    /**
     * Находит запись доступа пользователя к указанному курсу.
     */
    UserCourseAccessEntity findByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Удаляет запись доступа пользователя к указанному курсу.
     */
    void deleteByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Находит все записи доступа к указанному курсу.
     */
    List<UserCourseAccessEntity> findAllByCourseId(Long courseId);

}
