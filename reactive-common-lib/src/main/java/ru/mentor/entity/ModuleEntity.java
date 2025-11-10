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
 * Сущность модуля курса.
 * Представляет собой отдельный модуль учебного курса.
 */
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
    @Column("id_module")
    private Long id;

    /**
     * Название модуля.
     */
    @Column("module_title")
    private String moduleTitle;

    /**
     * Порядковый номер модуля в курсе.
     */
    @Column("module_number")
    private Integer moduleOrderNumber;

    /**
     * Содержание модуля.
     */
    @Column("module_content")
    private String moduleContent;

    /**
     * Флаг активности модуля.
     */
    @Column("is_active")
    private Boolean isActive;

    /**
     * Дата и время создания модуля.
     * Автоматически устанавливается при создании модуля.
     */
    @Column("created_at")
    private LocalDateTime createdAt;

    /**
     * Курс, к которому относится данный модуль.
     */
    @Column("id_course")
    private Long courseId;

}