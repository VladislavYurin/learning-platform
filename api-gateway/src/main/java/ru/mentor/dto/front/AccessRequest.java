package ru.mentor.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * Класс для управления доступом к курсу или модулю.
 */
@Data
@Builder
@Schema(description = "Запрос на управление доступом к курсу или модулю")
public class AccessRequest {

    @Schema(description = "ID пользователя", example = "123")
    @NotNull
    private Long userId;

    @Schema(description = "ID курса", example = "456")
    @NotNull
    private Long courseId;

    @Schema(description = "ID модуля (только для операций с модулями)", example = "789")
    private Long moduleId;

}
