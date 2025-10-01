package ru.mentor.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
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
public interface ModuleRepository extends ReactiveCrudRepository<ModuleEntity, Long> {

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

    /**
     * Находит все модули указанного курса, отсортированные по порядковому номеру.
     *
     * @param courseId
     *         идентификатор курса
     *
     * @return список модулей курса, отсортированный по порядковому номеру
     */
    Flux<ModuleEntity> findAllByCourseIdOrderByModuleOrderNumberAsc(Long courseId);

    /**
     * Возвращает количество модулей курса.
     *
     * @param courseId
     *          идентификатор курса
     *
     * @return {@link Mono}, содержащий количество модулей курса
     */
    Mono<Long> countByCourseId(Long courseId);
}
