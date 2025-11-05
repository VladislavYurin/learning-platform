package ru.mentor.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import ru.mentor.entity.CourseTagLinkEntity;

public interface CourseTagLinkRepository
        extends ReactiveCrudRepository<CourseTagLinkEntity, Long> {

    Flux<CourseTagLinkEntity> findByIdCourse(Long courseId);

}
