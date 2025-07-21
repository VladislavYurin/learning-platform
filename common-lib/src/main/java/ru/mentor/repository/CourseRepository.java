package ru.mentor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.CourseEntity;
import ru.mentor.exception.EntityNotFoundException;

@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    List<CourseEntity> findAllByIsActiveTrue();

    List<CourseEntity> findAllByIsActiveTrueAndAuthorId(Long userId);

    List<CourseEntity> findAllByAuthorId(Long userId);

    default CourseEntity findByIdOrThrow(Long courseId) {
        return this.findById(courseId)
                   .orElseThrow(() -> new EntityNotFoundException(
                           String.format(
                                   "Курс с ID = %d не найден",
                                   courseId
                           )
                   ));
    }

}
