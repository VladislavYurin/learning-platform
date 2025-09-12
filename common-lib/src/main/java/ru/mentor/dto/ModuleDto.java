package ru.mentor.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для передачи информации о модуле курса.
 * Содержит основные данные о модуле.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDto {

    /**
     * Уникальный идентификатор модуля.
     */
    private Long id;

    /**
     * Название модуля.
     */
    private String moduleTitle;

    /**
     * Порядковый номер модуля в курсе.
     */
    private Integer moduleOrderNumber;

    /**
     * Содержание модуля.
     */
    private String moduleContent;

    /**
     * Флаг активности модуля.
     */
    private Boolean isActive;

    /**
     * Дата и время создания модуля.
     */
    private LocalDateTime createdAt;

}
