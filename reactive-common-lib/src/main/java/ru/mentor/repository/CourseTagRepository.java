package ru.mentor.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mentor.entity.CourseTagEntity;
import ru.mentor.exception.EntityNotFoundException;

/**
 * Репозиторий хранит теги для курсов
 */
@Repository
public interface CourseTagRepository extends ReactiveCrudRepository<CourseTagEntity, Long>,
        ReactiveSortingRepository<CourseTagEntity, Long> {

    /**
     * Находит тег по его ID
     *
     * @param id тега
     *
     * @return Mono с сущностью тега
     */
    Mono<CourseTagEntity> findById(Long id);

    /**
     * Находит все теги с id, перечисленными в списке
     *
     * @param ids - список id тегов
     *
     * @return - список сущностей тегов
     */
    Flux<CourseTagEntity> findAllById(Iterable<Long> ids);

    /**
     * Находит тег по его id или выбрасывает исключение, если такого тега нет
     *
     * @param tagId - id тега в таблице course_tags
     *
     * @return - Mono с сущностью тега
     */
    default Mono<CourseTagEntity> findByIdOrThrow(Long tagId) {
        return this.findById(tagId)
                   .switchIfEmpty(Mono.error(new EntityNotFoundException(
                           String.format(
                                   "Тэг с [ ID = %d ] не найден",
                                   tagId
                           )
                   )));
    }

    /**
     * Находит все теги, которые есть у курса по ID курса
     *
     * @param courseId - ID курса в таблице courses
     *
     * @return - Flux с сущностями тегов курса
     */
    @Query("""
            SELECT course_tags.* FROM course_tags 
            JOIN course_tag_link ON course_tag_link.id_tag = course_tags.id_tag
            WHERE course_tag_link.id_course = :courseId
            """)
    Flux<CourseTagEntity> findAllByCourseId(Long courseId);
}
