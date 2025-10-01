package ru.mentor.dto;

import lombok.Builder;
import lombok.Data;

/**
 * DTO для внутреннего запроса на создание модуля.
 * Содержит информацию, необходимую для создания нового модуля курса.
 */
@Data
@Builder
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
