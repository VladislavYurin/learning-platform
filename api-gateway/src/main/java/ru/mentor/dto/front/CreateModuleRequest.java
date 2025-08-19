package ru.mentor.dto.front;

import lombok.Builder;
import lombok.Data;

/**
 * Класс для создания запроса на создание модуля внутри курса.
 * Используется как тело HTTP-запроса при создании/импорте модуля.
 */
@Data
@Builder
public class CreateModuleRequest {

    /**
     * Идентификатор курса, к которому относится создаваемый модуль.
     */
    private Long courseId;

    /**
     * Название модуля.
     */
    private String moduleTitle;

    /**
     * Порядковый номер модуля внутри курса.
     */
    private Integer moduleOrderNumber;

    /**
     * Содержимое модуля.
     */
    private String moduleContent;

}
