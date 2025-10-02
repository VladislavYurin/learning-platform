package ru.mentor.dto.mentorTag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "DTO для запроса привязки тэгов к ментору")
public class MentorTagDetachRequestDto {
    @Schema(description = "ID ментора от которого отвязывают тэг", example = "1")
    private Long mentorId;

    @Schema(description = "ID тэга", example = "2")
    private Long tagId;
}