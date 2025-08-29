package ru.mentor.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Сущность курса.
 * Представляет собой учебный курс, состоящий из нескольких модулей и созданный автором.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "courses")
public class CourseEntity {

    /**
     * Уникальный идентификатор курса.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_course")
    private Long id;

    /**
     * Название курса.
     * Обязательное поле, содержащее заголовок курса.
     */
    @Column(name = "course_title", nullable = false)
    private String courseTitle;

    /**
     * Описание курса.
     */
    @Column(name = "description")
    private String description;

    /**
     * Флаг активности курса.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Дата и время создания курса.
     * Автоматически устанавливается при создании курса.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Автор курса.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_author_id", referencedColumnName = "id_user")
    private UserEntity author;

    /**
     * Список модулей курса.
     * Содержит все модули, входящие в состав курса.
     * При удалении курса все его модули также удаляются.
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ModuleEntity> modules = new ArrayList<>();

}
