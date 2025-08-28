package ru.mentor.dto.front;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * DTO для запроса управления доступом к курсам и модулям.
 * Используется для предоставления или отзыва доступа пользователей
 * к определенным курсам или их модулям.
 * 
 * @author API Gateway Team
 * @version 1.0
 * @since 1.0
 */
@Data
@Builder
@Schema(description = "Запрос на управление доступом к курсу или модулю")
public class AccessRequest {

    /**
     * Идентификатор пользователя, которому предоставляется или отзывается доступ.
     */
    @Schema(description = "ID пользователя", example = "123")
    @NotNull
    private Long userId;

    /**
     * Идентификатор курса, к которому управляется доступ.
     */
    @Schema(description = "ID курса", example = "456")
    @NotNull
    private Long courseId;

    /**
     * Идентификатор модуля (опционально).
     * Указывается только при управлении доступом к конкретному модулю курса.
     */
    @Schema(description = "ID модуля (только для операций с модулями)", example = "789")
    @NotNull
    private Long moduleId;

}
