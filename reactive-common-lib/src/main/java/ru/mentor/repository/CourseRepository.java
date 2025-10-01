package ru.mentor.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mentor.entity.CourseEntity;
import ru.mentor.exception.EntityNotFoundException;

/**
 * Репозиторий для работы с сущностями курсов.
 * Предоставляет методы для выполнения CRUD операций и дополнительные методы
 * для поиска курсов по различным критериям.
 */
@Repository
public interface CourseRepository
        extends ReactiveCrudRepository<CourseEntity, Long>,
                        ReactiveSortingRepository<CourseEntity, Long> {

    /**
     * Находит курс по его идентификатору или выбрасывает исключение, если курс не найден.
     *
     * @param courseId
     *         идентификатор курса
     *
     * @return сущность курса
     *
     * @throws EntityNotFoundException
     *         если курс с указанным идентификатором не найден
     */
    default Mono<CourseEntity> findByIdOrThrow(Long courseId) {
        return this.findById(courseId)
                   .switchIfEmpty(Mono.error(new EntityNotFoundException(
                           String.format(
                                   "Курс с ID = %d не найден",
                                   courseId
                           )
                   )));
    }

    /**
     * Находит все курсы.
     *
     * @param pageable
     *         параметры страницы
     *
     * @return список всех курсов постранично
     */
    Flux<CourseEntity> findAllBy(Pageable pageable);

}
