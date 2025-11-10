package ru.mentor.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Сущность курса.
 * Представляет собой учебный курс, состоящий из нескольких модулей и созданный автором.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "courses")
public class CourseEntity {

    /**
     * Уникальный идентификатор курса.
     */
    @Id
    @Column("id_course")
    private Long id;

    /**
     * Название курса.
     * Обязательное поле, содержащее заголовок курса.
     */
    @Column("course_title")
    private String courseTitle;

    /**
     * Описание курса.
     */
    @Column("description")
    private String description;

    /**
     * Флаг активности курса.
     */
    @Column("is_active")
    private Boolean isActive;

    /**
     * Дата и время создания курса.
     * Автоматически устанавливается при создании курса.
     */
    @Column("created_at")
    private LocalDateTime createdAt;

    /**
     * Автор курса.
     */
    @Column("course_author_id")
    private Long authorId;

}
