package ru.mentor.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@Schema(description = "Запрос на создание модуля курса")
public class CreateModuleRequest {

    /**
     * Идентификатор курса, к которому принадлежит создаваемый модуль.
     */
    @Schema(description = "ID курса", example = "123")
    @NotNull(message = "ID курса не может быть пустым")
    @Positive(message = "ID курса должно быть положительным числом")
    private Long courseId;

    /**
     * Название модуля.
     */
    @Schema(description = "Название модуля", example = "Модуль 1: Основы Spring Boot")
    @NotBlank(message = "Название модуля не может быть пустым")
    private String moduleTitle;

    /**
     * Порядковый номер модуля в рамках курса.
     */
    @Schema(description = "Порядковый номер модуля в курсе", example = "1")
    @NotNull(message = "Порядковый номер модуля не может быть пустым")
    @Positive(message = "Порядковый номер модуля должен быть положительным числом")
    private Integer moduleOrderNumber;


    /**
     * Содержимое модуля в формате Markdown.
     */
    @Schema(description = "Содержимое модуля в формате Markdown", example = "# Введение\n\nЭтот модуль содержит основы Spring Boot...")
    @NotBlank(message = "Содержимое модуля не может быть пустым")
    private String moduleContentDescription;

}
