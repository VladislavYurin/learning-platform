package ru.mentor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mentor.entity.CourseTagEntity;

@Repository
public interface CourseTagRepository extends JpaRepository<CourseTagEntity, Long> {

    /**
     * Проверяет, существует ли тег с указанным именем (без учёта регистра).
     *
     * @param tagName имя тега
     * @return {@code true}, если тег с таким именем существует,
     *         {@code false} в противном случае
     */
    boolean existsByTagNameIgnoreCase(String tagName);
}
