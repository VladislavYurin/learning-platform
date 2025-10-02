package ru.mentor.dto.mentorTag;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "DTO ответа для запроса привязки тэгов к ментору")
public class MentorTagDetachResponseDto {

    @Schema(description = "Уникальный идентификатор запроса", example = "213")
    private String rqUid;

    @Schema(description = "ID ментора от которого был отвязан тэг", example = "1")
    private Long mentorId;

    @Schema(description = "ID отвязанного тэга", example = "2")
    private Long tagIds;
}
