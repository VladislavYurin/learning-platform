package ru.mentor.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Запрос на управление доступом к модулю курса")
public class ModuleAccessRequest extends CourseAccessRequest {

    /**
     * Идентификатор модуля.
     */
    @Schema(description = "ID модуля", example = "789")
    private Long moduleId;
}
