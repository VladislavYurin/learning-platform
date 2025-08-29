package ru.mentor.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.CourseEntity;
import ru.mentor.exception.EntityNotFoundException;

/**
 * Репозиторий для работы с сущностями курсов.
 * Предоставляет методы для выполнения CRUD операций и дополнительные методы
 * для поиска курсов по различным критериям.
 */
@Repository
public interface CourseRepository extends JpaRepository<CourseEntity, Long> {

    /**
     * Находит все активные курсы.
     *
     * @return список активных курсов
     */
    List<CourseEntity> findAllByIsActiveTrue();

    /**
     * Находит все активные курсы, созданные указанным пользователем.
     *
     * @param userId идентификатор пользователя-автора
     * @return список активных курсов указанного автора
     */
    List<CourseEntity> findAllByIsActiveTrueAndAuthorId(Long userId);

    /**
     * Находит все курсы, созданные указанным пользователем.
     *
     * @param userId идентификатор пользователя-автора
     * @return список всех курсов указанного автора
     */
    List<CourseEntity> findAllByAuthorId(Long userId);

    /**
     * Находит курс по его идентификатору или выбрасывает исключение, если курс не найден.
     *
     * @param courseId идентификатор курса
     * @return сущность курса
     * @throws EntityNotFoundException если курс с указанным идентификатором не найден
     */
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
