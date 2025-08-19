package ru.mentor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Сущность модуля курса.
 * Представляет собой отдельный модуль учебного курса.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "modules")
public class ModuleEntity {

    /**
     * Уникальный идентификатор модуля.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_module")
    private Long id;

    /**
     * Название модуля.
     */
    @Column(name = "module_title", nullable = false)
    private String moduleTitle;

    /**
     * Порядковый номер модуля в курсе.
     */
    @Column(name = "module_number", nullable = false)
    private Integer moduleOrderNumber;

    /**
     * Содержание модуля.
     */
    @Column(name = "module_content", columnDefinition = "TEXT")
    private String moduleContent;

    /**
     * Флаг активности модуля.
     */
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * Дата и время создания модуля.
     * Автоматически устанавливается при создании модуля.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Курс, к которому относится данный модуль.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_course", nullable = false)
    private CourseEntity course;

}