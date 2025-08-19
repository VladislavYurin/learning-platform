package ru.mentor.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.UserModuleAccessEntity;

/**
 * Репозиторий для работы с сущностями доступа пользователей к модулям.
 * Предоставляет методы для выполнения CRUD операций и дополнительные методы
 * для управления доступом пользователей к модулям курсов.
 */
@Repository
public interface UserModuleAccessRepository extends JpaRepository<UserModuleAccessEntity, Long> {

    /**
     * Проверяет, существует ли доступ пользователя к указанному модулю.
     */
    boolean existsByUserIdAndModuleId(Long userId, Long moduleId);

    /**
     * Удаляет все записи доступа пользователя к модулям указанного курса.
     */
    void deleteAllByUserIdAndCourseId(Long userId, Long courseId);

    /**
     * Удаляет запись доступа пользователя к указанному модулю.
     */
    void deleteByUserIdAndModuleId(Long userId, Long moduleId);

    /**
     * Находит все записи доступа к модулям указанного курса.
     */
    List<UserModuleAccessEntity> findAllByCourseId(Long courseId);

    /**
     * Находит последнюю запись доступа пользователя к модулям указанного курса,
     * отсортированную по дате предоставления доступа в порядке убывания.
     */
    Optional<UserModuleAccessEntity> findTopByUserIdAndCourseIdOrderByAccessGrantedAtDesc(Long id, Long courseId);

    /**
     * Находит все записи доступа пользователя к модулям указанного курса.
     */
    List<UserModuleAccessEntity> findAllByUserIdAndCourseId(Long id, Long courseId);

}