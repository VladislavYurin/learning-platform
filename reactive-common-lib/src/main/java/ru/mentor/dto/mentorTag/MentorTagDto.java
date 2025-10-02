package ru.mentor.dto.mentorTag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import ru.mentor.constant.MentorTagType;

/**
 * DTO тега ментора.
 * Используется в сервисах/бизнес-логике, а также как промежуточная модель
 * перед отдачей наружу по gRPC/REST.
 */
@Data
@Builder
@Schema(description = "Информация о тэге ментора")
public class MentorTagDto {

    @Schema(description = "Уникальный идентификатор тэга", example = "1")
    private Long id;

    @Schema(description = "Имя-описание тэга", example = "Ментор года села Алупки")
    private String tagName;

    @Schema(description = "Тип тэга", example = "BADGE")
    private MentorTagType type;
}
