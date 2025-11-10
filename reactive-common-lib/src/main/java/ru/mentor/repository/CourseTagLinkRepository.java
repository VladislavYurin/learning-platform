package ru.mentor.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.mentor.entity.CourseTagLinkEntity;

/**
 * Репозиторий хранит связи курсов с тегами
 */
public interface CourseTagLinkRepository
        extends ReactiveCrudRepository<CourseTagLinkEntity, Long> {

    /**
     * Находит все теги курса
     *
     * @param courseId - ID курса в таблице courses
     *
     * @return - Flux с сущностями тегов курса
     */
    Flux<CourseTagLinkEntity> findByIdCourse(Long courseId);

}
