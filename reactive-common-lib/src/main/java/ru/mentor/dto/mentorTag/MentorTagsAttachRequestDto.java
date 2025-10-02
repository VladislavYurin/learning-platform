package ru.mentor.dto.mentorTag;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * DTO для POST-запроса привязки тэга к ментору
 */
@Data
@Builder
@Schema(description = "DTO для запроса привязки тэгов к ментору")
public class MentorTagsAttachRequestDto {
    @Schema(description = "ID ментора к которому привязывают теги", example = "1")
    private Long mentorId;

    @Schema(description = "Список ID тегов для привязки", example = "[1,2,3]")
    private List<Long> tagsIds;
}
