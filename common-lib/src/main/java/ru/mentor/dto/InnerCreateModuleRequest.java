package ru.mentor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для внутреннего запроса на создание модуля.
 * Содержит информацию, необходимую для создания нового модуля курса.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InnerCreateModuleRequest {

    /**
     * Идентификатор пользователя, создающего модуль.
     */
    private Long userId;

    /**
     * Идентификатор курса, к которому относится модуль.
     */
    private Long courseId;

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

}
