package ru.mentor.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mentor.entity.UserModuleAccessEntity;

/**
 * Репозиторий хранит данные о выданных пользователям доступах к модулям курсов
 */
public interface UserModuleAccessRepository
        extends ReactiveCrudRepository<UserModuleAccessEntity, Long> {

    /**
     * Возвращает все модули курса, к которым у пользователя есть доступ
     *
     * @param id - ID пользователя в таблице users
     * @param courseId - ID курса в таблице courses
     *
     * @return - Flux с сущностями доступов к модулям
     */
    Flux<UserModuleAccessEntity> findAllByUserIdAndCourseId(Long id, Long courseId);

    /**
     * Проверяет, есть ли у пользователя доступ к модулю курса
     *
     * @param userId - ID пользователя в таблице users
     * @param courseId - ID курса в таблице courses
     * @param moduleOrderNum - порядковый номер модуля курса
     *
     * @return - Mono с true, если доступ есть, иначе false
     */
    @Query("""
            SELECT EXISTS(
            SELECT 1 FROM user_module_access uma
                    JOIN modules m ON m.id_module = uma.module_id
                    WHERE uma.user_id = :userId
                      AND uma.course_id = :courseId
                      AND m.module_number = :moduleOrderNum)
            """)
    Mono<Boolean> existsByUserIdAndCourseIdAndModuleOrderNum(
            Long userId,
            Long courseId,
            Integer moduleOrderNum);

}
