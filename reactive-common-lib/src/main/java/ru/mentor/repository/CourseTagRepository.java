package ru.mentor.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mentor.entity.CourseTagEntity;

@Repository
public interface CourseTagRepository extends ReactiveSortingRepository<CourseTagEntity, Long> {

    Mono<CourseTagEntity> findById(Long id);

    @Query("""
            SELECT * FROM course_tags 
            JOIN course_tag_link ON course_tag_link.id_tag = course_tags.id_tag
            WHERE course_tag_link.id_course = :courseId
            """)
    Flux<CourseTagEntity> findAllByCourseId(Long courseId);
}
