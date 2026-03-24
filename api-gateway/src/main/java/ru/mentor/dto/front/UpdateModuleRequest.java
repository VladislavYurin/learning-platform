package ru.mentor.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

/**
 * DTO для запроса на обновление модуля от клиента.
 * Содержит данные для полного обновления модуля курса.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@Schema(description = "Запрос на обновление модуля курса")
public class UpdateModuleRequest extends CreateModuleRequest {

    /**
     * Идентификатор модуля, который нужно обновить.
     */
    @Schema(description = "ID модуля", example = "456")
    @NotNull(message = "ID модуля не может быть пустым")
    @Positive(message = "ID модуля должно быть положительным числом")
    private Long moduleId;

    /**
     * Флаг активности модуля.
     */
    @Schema(description = "Признак активности модуля", example = "true")
    @NotNull(message = "Признак активности модуля не может быть пустым")
    private Boolean isActive;

}
