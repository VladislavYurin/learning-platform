package ru.mentor.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mentor.entity.ModuleEntity;
import ru.mentor.exception.EntityNotFoundException;

/**
 * Репозиторий для работы с сущностями модулей.
 * Предоставляет методы для выполнения CRUD операций и дополнительные методы
 * для поиска модулей по различным критериям.
 */
@Repository
public interface ModuleRepository extends ReactiveCrudRepository<ModuleEntity, Long>,
        ReactiveSortingRepository<ModuleEntity, Long> {

    /**
     * Находит модуль по его идентификатору или выбрасывает исключение, если модуль не найден.
     *
     * @param moduleId
     *         идентификатор модуля
     *
     * @return сущность модуля
     *
     * @throws EntityNotFoundException
     *         если модуль с указанным идентификатором не найден
     */
    default Mono<ModuleEntity> findByIdOrThrow(Long moduleId) {
        return this.findById(moduleId)
                   .switchIfEmpty(Mono.error(new EntityNotFoundException(
                           String.format(
                                   "Модуль с ID = %d не найден",
                                   moduleId
                           )
                   )));
    }

    default Mono<ModuleEntity> findByCourseIdAndModuleOrderNumberOrThrow(Long courseId, Integer moduleOrderNumber) {
        return this.findByCourseIdAndModuleOrderNumber(courseId, moduleOrderNumber)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        String.format(
                                "Модуль [ num = %d ] у курса [ ID = %d ] не найден",
                                moduleOrderNumber, courseId
                        )
                )));
    }

    Flux<ModuleEntity> findAllBy(Pageable pageable);

    Flux<ModuleEntity> findAllByCourseId(Long courseId);

    @Query("""
            SELECT m.* FROM modules m
            JOIN user_module_access uma ON m.id_module = uma.module_id
            WHERE uma.user_id = :userId
            AND uma.course_id = :courseId
            ORDER BY m.module_number
            """)
    Flux<ModuleEntity> findAllAccessibleModules(Long courseId, Long userId);

    Mono<ModuleEntity> findByCourseIdAndModuleOrderNumber(Long courseId, Integer moduleOrderNumber);

    Mono<Boolean> existsByCourseIdAndModuleOrderNumber(Long courseId, Integer moduleOrderNumber);
}
